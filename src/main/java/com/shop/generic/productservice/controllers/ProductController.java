package com.shop.generic.productservice.controllers;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
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
    public ResponseEntity<RestApiResponse<List<ProductVO>>> retrieveAllProducts() {
        log.info("Request made to get all products");
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(
                this.productService.findAllProducts()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<RestApiResponse<ProductVO>> retrieveProductById(
            @PathVariable final String productId) throws ProductDoesNotExistException {
        log.info("Request made to find product {}", productId);
        final ProductVO productVO = this.productService.retrieveProductById(
                Integer.parseInt(productId));
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(productVO));
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
    public ResponseEntity<RestApiResponse<List<ProductVO>>> retrieveProductDetails(
            @PathVariable final List<String> productIds) throws ProductDoesNotExistException {
        log.info("Fetching details for products: {}", productIds);
        final List<ProductVO> productVOS = this.productService.fetchProductDetails(productIds);
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(productVOS));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse<List<ProductVO>>> getProducts(
            @RequestParam final List<Integer> productIds) {
        log.info("Request made to fetch products {}", productIds);
        final List<ProductVO> products = productService.getProductsByIds(productIds);
        return ResponseEntity.ok(this.restApiResponseFactory.createSuccessResponse(products));
    }
}
