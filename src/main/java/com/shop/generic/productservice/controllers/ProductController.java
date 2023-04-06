package com.shop.generic.productservice.controllers;

import com.shop.generic.common.dtos.ProductDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.services.ProductService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final RestApiResponseFactory restApiResponseFactory;

    public ProductController(final ProductService productService,
            final RestApiResponseFactory restApiResponseFactory) {
        this.productService = productService;
        this.restApiResponseFactory = restApiResponseFactory;
    }

    @GetMapping("/all")
    public ResponseEntity<RestApiResponse<List<ProductDTO>>> retrieveAllProducts() {
        log.info("Request made to get all products");
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(
                this.productService.findAllProducts()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<RestApiResponse<ProductDTO>> retrieveProductById(
            @PathVariable final String productId) throws ProductDoesNotExistException {
        log.info("Request made to find product {}", productId);
        final ProductDTO productDTO = this.productService.retrieveProductById(
                Integer.parseInt(productId));
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(productDTO));
    }

    @Transactional
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse<String>> updateProducts(
            @RequestBody final List<PurchaseProductDTO> purchaseProductDTOS)
            throws ProductDoesNotExistException {
        log.info("Received request to update products: {}", purchaseProductDTOS);
        this.productService.updateProductsStock(purchaseProductDTOS);
        return ResponseEntity.ok(
                restApiResponseFactory.createSuccessResponse("Product stock successfully updated"));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<ProductDTO>>> getProducts(
            @RequestParam final List<Integer> productIds) {
        log.info("Request made to fetch products {}", productIds);
        final List<ProductDTO> products = productService.getProductsByIds(productIds);
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(products));
    }
}
