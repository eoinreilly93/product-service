package com.shop.generic.productservice.repositories;

import static org.jeasy.random.FieldPredicates.named;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.enums.StockStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * These tests will by default use our main app schema.sql and data.sql files. We could also make
 * use of @Sql annotation or even a test data.sql file to setup dummy data for our tests
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void should_findAllProducts() {
        final Product p = new Product();
        p.setName("Test");
        p.setPrice(BigDecimal.TEN);
        p.setStockCount(100);
        p.setStockStatus(StockStatus.AVAILABLE);
        testEntityManager.persist(p);
        assertEquals(1, this.productRepository.findAll().size());
    }

    @Test
    public void should_findProductsById() {
        final EasyRandomParameters parameters = new EasyRandomParameters();
        //We have to exclude the productId as it is auto generated. If we don't, well get a "detached entity passed to persist" error
        parameters.excludeField(named("productId"));
        final EasyRandom generator = new EasyRandom(parameters);
        final List<Product> productList = generator.objects(Product.class, 20).toList();
        productList.forEach(product -> testEntityManager.persist(product));

        final List<Integer> productIds = productList.stream().map(Product::getProductId).toList();
        assertEquals(20, this.productRepository.findAllById(productIds).size());
    }

    @Test
    public void should_findAProductById() {
        final EasyRandomParameters parameters = new EasyRandomParameters();
        //We have to exclude the productId as it is auto generated. If we don't, well get a "detached entity passed to persist" error
        parameters.excludeField(named("productId"));
        final EasyRandom generator = new EasyRandom(parameters);
        final List<Product> productList = generator.objects(Product.class, 20).toList();
        productList.forEach(product -> testEntityManager.persist(product));

        final Product product = productList.get(12);
        final Optional<Product> result = this.productRepository.findById(product.getProductId());
        if (result.isPresent()) {
            assertEquals(product, result.get());
        } else {
            fail();
        }
    }
}