package com.shop.generic.productservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.shop.generic.common.dtos.ProductDTO;
import com.shop.generic.common.entities.Product;
import com.shop.generic.common.enums.StockStatus;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.repositories.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
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

        final List<ProductDTO> result = this.productService.findAllProducts();
        assertEquals(result.size(), 5);
    }

    @Test
    @DisplayName("Service should throw ProductDoesNotExistException if given invalid or unknown product id")
    public void should_throwProductDoesNotExistException() {
        given(this.productRepository.findById(1)).willReturn(Optional.empty());

        final Exception exception = assertThrows(ProductDoesNotExistException.class,
                () -> this.productService.retrieveProductById(1));

        assertEquals("No product with Product ID 1 exists", exception.getMessage());
    }

    @Test
    @DisplayName("Service should find a product given a valid product id")
    public void should_findAProduct_ById() {
        final Product p = new Product();
        p.setProductId(1);
        p.setName("Test product");
        p.setPrice(BigDecimal.TEN);
        p.setStockStatus(StockStatus.AVAILABLE);
        p.setStockCount(100);
        final Optional<Product> optional = Optional.of(p);
        given(this.productRepository.findById(1)).willReturn(optional);

        //When
        this.productService.retrieveProductById(1);

        //Then
        verify(this.productRepository, times(1)).findById(1);
    }
}