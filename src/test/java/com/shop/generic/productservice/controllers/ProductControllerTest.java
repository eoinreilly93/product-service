package com.shop.generic.productservice.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.generic.common.enums.StockStatus;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.productservice.services.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
 */

@AutoConfigureJsonTesters
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<
            RestApiResponse
                    <List<ProductVO>>> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private RestApiResponseFactory restApiResponseFactory;

    @Test
    public void should_ReturnAllProducts() throws Exception {

        final ProductVO productVO1 = new ProductVO("Test", BigDecimal.TEN, StockStatus.AVAILABLE,
                100);
        final ProductVO productVO2 = new ProductVO("Test 2", BigDecimal.ONE, StockStatus.AVAILABLE,
                150);

        final List<ProductVO> productVOList = List.of(productVO1, productVO2);
        final RestApiResponse<List<ProductVO>> mockApiResponse = new RestApiResponse<>(null, null,
                productVOList,
                LocalDateTime.now());

        given(productService.findAllProducts())
                .willReturn(productVOList);
        given(restApiResponseFactory.createSuccessResponse(
                productVOList))
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
        final RestApiResponse<List<ProductVO>> responseAsObject = objectMapper.readValue(
                responseAsString,
                new TypeReference<>() {
                });

        assertThat(responseAsObject.getResult()).usingRecursiveComparison()
                .isEqualTo(mockApiResponse.getResult());
    }
}