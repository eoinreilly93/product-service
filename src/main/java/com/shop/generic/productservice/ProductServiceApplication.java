package com.shop.generic.productservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.shop.generic")
@EntityScan("com.shop.generic.common.entities")
public class ProductServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
