package com.shop.generic.productservice.controllers;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.services.ProductService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<ProductVO> retrieveAllAvailableProducts() {
        log.info("Request made to get all products");
        return this.productService.findAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity retrieveProductById(@PathVariable final String productId) {
        log.info("Request made to find product {}", productId);
        try {
            final ProductVO productVO = this.productService.retrieveProductById(
                    Integer.parseInt(productId));
            return ResponseEntity.ok().body(productVO);
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Transactional
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestApiResponse<String>> updateProducts(
            @RequestBody final List<PurchaseProductVO> purchaseProductVOS)
            throws ProductDoesNotExistException {
        log.info("Received request to update products: {}", purchaseProductVOS);
        this.productService.updateProductsStock(purchaseProductVOS);
        return ResponseEntity.ok(
                restApiResponseFactory.createSuccessResponse("Product stock successfully updated"));
    }

    @GetMapping(value = "/{productIds}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity retrieveProductDetails(
            @PathVariable final List<String> productIds) {
        log.info("Checking availability status for product ids: {}", productIds);
        try {
            final List<ProductVO> productVOS = this.productService.fetchProductDetails(productIds);
            return ResponseEntity.ok(productVOS);
        } catch (final ProductDoesNotExistException e) {
            //TODO: Handle this in controller advice
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
