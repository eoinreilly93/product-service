package com.shop.generic.productservice.services;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.repositories.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductVO> findAllProducts() {
        return this.productRepository.findAll().stream().map(ProductVO::new).toList();
    }

    public ProductVO retrieveProductById(final int productId) throws ProductDoesNotExistException {
        final Product product = findProductById(productId).orElseThrow(
                () -> new ProductDoesNotExistException(productId));
        return new ProductVO(product);
    }

    public void updateProductsStock(final List<PurchaseProductVO> purchaseProductVOS)
            throws ProductDoesNotExistException {

        for (final PurchaseProductVO purchaseProductVO : purchaseProductVOS) {
            //Retrieve stock count for product id
            //TODO: Replace exception handling here with same logic above
            final Optional<Product> product = findProductById(purchaseProductVO.productId());

            if (product.isPresent()) {
                final int newStockCountValue =
                        product.get().getStockCount() - purchaseProductVO.quantity();
                //Update the stock of that product id
                log.info("Updating product {} stock value from {} to {}",
                        purchaseProductVO.productId(), product.get().getStockCount(),
                        newStockCountValue);
                this.productRepository.updateStockCountByProductId(newStockCountValue,
                        purchaseProductVO.productId());
                log.info("Product {} stock updated", purchaseProductVO.productId());
            } else {
                throw new ProductDoesNotExistException(purchaseProductVO.productId());
            }
        }
    }

    //TODO: This can probably be removed
    private Optional<Product> findProductById(final int productId) {
        return this.productRepository.findById(productId);
    }

    public List<ProductVO> fetchProductDetails(final List<String> productIds)
            throws ProductDoesNotExistException {
        final List<ProductVO> productVOS = new ArrayList<>();
        //This can't be written using Streams API because if you look at the Consumer<T> interface,
        // the accept method (which is what this method reference would effectively be using) isn't declared to throw
        // any checked exceptions - therefore you can't use a method reference which is declared to throw a checked exception
        for (final String s : productIds) {
            final ProductVO productVO = retrieveProductById(Integer.parseInt(s));
            productVOS.add(productVO);
        }
        return productVOS;
    }

    public List<ProductVO> getProductsByIds(final List<Integer> productIds) {
        final List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            final List<Integer> missingIds = productIds.stream()
                    .filter(id -> products.stream().noneMatch(p -> p.getProductId().equals(id)))
                    .toList();
            throw new ProductDoesNotExistException(missingIds);
        }
        return products.stream().map(ProductVO::new).toList();
    }
}
