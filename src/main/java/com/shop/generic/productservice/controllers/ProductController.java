package com.shop.generic.productservice.controllers;

import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
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

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public List<ProductVO> retrieveAllAvailableProducts() {
        log.info("Request made to get all products");
        return this.productService.findAllAvailableProducts();
    }

    @GetMapping("/{productId}")
    public ProductVO retrieveProductById(@PathVariable final String productId) {
        log.info("Request made to find product {}", productId);
        return this.productService.findProductById(Integer.parseInt(productId));
    }

    @Transactional
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProducts(
            @RequestBody final List<PurchaseProductVO> purchaseProductVOS) {
        log.info("Updating products");
        try {
            this.productService.updateProductsStock(purchaseProductVOS);
            return ResponseEntity.ok().body("Products updated");
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
