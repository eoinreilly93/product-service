package com.shop.generic.productservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.repositories.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    //Could also use construction injection for the mocks here
    @InjectMocks
    private ProductService productService;

    @Test
    public void should_findAllProducts_asProductVOs() {
        final EasyRandom generator = new EasyRandom();
        final List<Product> productList = generator.objects(Product.class, 5).toList();
        given(this.productRepository.findAll()).willReturn(productList);

        final List<ProductVO> result = this.productService.findAllProducts();
        assertEquals(result.size(), 5);
    }

    @Test
    public void should_throwProductDoesNotExistException() {
        given(this.productRepository.findById(1)).willReturn(Optional.empty());

        final Exception exception = assertThrows(ProductDoesNotExistException.class,
                () -> this.productService.retrieveProductById(1));

        assertEquals("No product with Product ID 1 exists", exception.getMessage());
    }
}