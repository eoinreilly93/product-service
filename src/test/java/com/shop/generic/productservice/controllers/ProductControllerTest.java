package com.shop.generic.productservice.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.generic.common.dtos.ProductDTO;
import com.shop.generic.common.enums.StockStatus;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.services.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Here we test with @WebMvcTest, which does not start an HTTP Server, so we can use MockMvc to
 * perform the requests. The annotation only loads a partial context (the controller and it's
 * surrounding configuration like filters and advices) We also don't need to include SpringExtension
 * here as it is included by default on all Spring's @...Test shortcut annotations
 * <p>
 * The order of these annotations matters. If you put @AutoConfigureJsonTesters it doesn't exclude
 * the SecurityAutoConfiguration for some reason, adn the tests will fail due to spring security
 */

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureJsonTesters
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<
            RestApiResponse
                    <List<ProductDTO>>> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private RestApiResponseFactory restApiResponseFactory;

    @Test
    public void should_ReturnAllProducts() throws Exception {

        final ProductDTO productDTO1 = new ProductDTO(1, "Test", BigDecimal.TEN,
                StockStatus.AVAILABLE,
                100);
        final ProductDTO productDTO2 = new ProductDTO(2, "Test 2", BigDecimal.ONE,
                StockStatus.AVAILABLE,
                150);

        final List<ProductDTO> productDTOList = List.of(productDTO1, productDTO2);
        final RestApiResponse<List<ProductDTO>> mockApiResponse = new RestApiResponse<>(null, null,
                productDTOList,
                LocalDateTime.now());

        given(productService.findAllProducts())
                .willReturn(productDTOList);
        given(restApiResponseFactory.createSuccessResponse(
                productDTOList))
                .willReturn(mockApiResponse);

        /**
         * First way of testing using raw json
         */
        this.mockMvc.perform(get("/products/all").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("Test"))
                .andExpect(jsonPath("$.result[1].name").value("Test 2"))
                .andExpect(content().string(containsString("")));

        /**
         * A second way of testing the above
         */
        final MockHttpServletResponse response = this.mockMvc.perform(
                        get("/products/all").contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jacksonTester.write(mockApiResponse).getJson());

        /**
         * Third way of testing by comparing the actual objects
         */
        final String responseAsString = response.getContentAsString();
        final RestApiResponse<List<ProductDTO>> responseAsObject = objectMapper.readValue(
                responseAsString,
                new TypeReference<>() {
                });

        assertThat(responseAsObject.getResult()).usingRecursiveComparison()
                .isEqualTo(mockApiResponse.getResult());
    }

    @Test
    @DisplayName("Controller returns a 400 exception when a product does not exist")
    public void should_return4xxForNoProduct() throws Exception {
        final RestApiResponse mockApiResponse = new RestApiResponse<>(null,
                "Product with id 1 does not exist",
                null,
                LocalDateTime.now());

        given(productService.retrieveProductById(1)).willThrow(
                new ProductDoesNotExistException(1));

        given(restApiResponseFactory.createErrorResponse(
                "No product with Product ID 1 exists")).willReturn(mockApiResponse);

        //When
        final MockHttpServletResponse response = this.mockMvc.perform(
                        get("/products/1").contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jacksonTester.write(mockApiResponse).getJson());
    }

    @Test
    @DisplayName("Controller returns 500 for all other exceptions")
    public void should_return500ForOtherExceptions() throws Exception {
        given(productService.retrieveProductById(anyInt())).willThrow(RuntimeException.class);

        //When
        final MockHttpServletResponse response = this.mockMvc.perform(
                        get("/products/1").contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("Controller returns 200 with the product information when it exists")
    public void should_return200WhenProductExists() throws Exception {
        final ProductDTO productDTO1 = new ProductDTO(1, "Test product", BigDecimal.TEN,
                StockStatus.AVAILABLE,
                100);

        final RestApiResponse<ProductDTO> mockApiResponse = new RestApiResponse<>(null, null,
                productDTO1,
                LocalDateTime.now());

        given(productService.retrieveProductById(1))
                .willReturn(productDTO1);
        given(restApiResponseFactory.createSuccessResponse(
                productDTO1))
                .willReturn(mockApiResponse);

        //When
        final MockHttpServletResponse response = this.mockMvc.perform(
                        get("/products/1").contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //Then
        final String responseAsString = response.getContentAsString();
        final RestApiResponse<ProductDTO> responseAsObject = objectMapper.readValue(
                responseAsString,
                new TypeReference<>() {
                });

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseAsObject.getResult()).usingRecursiveComparison()
                .isEqualTo(mockApiResponse.getResult());

    }

    @Test
    public void should_return200WhenProductsExists() throws Exception {
        final ProductDTO productDTO1 = new ProductDTO(1, "Test product", BigDecimal.TEN,
                StockStatus.AVAILABLE,
                100);
        final ProductDTO productDTO2 = new ProductDTO(2, "Test product 2", BigDecimal.ONE,
                StockStatus.AVAILABLE,
                150);

        final List<ProductDTO> productDTOList = List.of(productDTO1, productDTO2);
        final RestApiResponse<List<ProductDTO>> mockApiResponse = new RestApiResponse<>(null, null,
                productDTOList,
                LocalDateTime.now());

        given(productService.getProductsByIds(List.of(1, 2)))
                .willReturn(productDTOList);
        given(restApiResponseFactory.createSuccessResponse(
                productDTOList))
                .willReturn(mockApiResponse);

        //When
        final MockHttpServletResponse response = this.mockMvc.perform(
                        get("/products?productIds=1,2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        //Then
        final String responseAsString = response.getContentAsString();
        final RestApiResponse<List<ProductDTO>> responseAsObject = objectMapper.readValue(
                responseAsString,
                new TypeReference<>() {
                });

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseAsObject.getResult()).usingRecursiveComparison()
                .isEqualTo(mockApiResponse.getResult());
    }
}