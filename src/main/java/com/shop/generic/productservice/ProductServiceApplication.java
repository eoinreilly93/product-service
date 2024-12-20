package com.shop.generic.productservice;

import com.shop.generic.common.CommonKafkaConsumerAutoConfiguration;
import com.shop.generic.common.CommonKafkaProducerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(exclude = {CommonKafkaProducerAutoConfiguration.class,
        CommonKafkaConsumerAutoConfiguration.class})
@EntityScan("com.shop.generic.common.entities")
public class ProductServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
