package com.shop.generic.productservice.services;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.productservice.repositories.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductVO> findAllAvailableProducts() {
        return this.productRepository.findAll().stream().map(ProductVO::new).toList();
    }

    public ProductVO findProductById(int productId) {
        Product product = this.productRepository.findById(productId).orElse(null);
        return new ProductVO(product);
    }
}
