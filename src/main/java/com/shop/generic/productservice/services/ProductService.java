package com.shop.generic.productservice.services;

import com.shop.generic.common.dtos.ProductDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.entities.Product;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import com.shop.generic.productservice.repositories.ProductRepository;
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

    public List<ProductDTO> findAllProducts() {
        return this.productRepository.findAll().stream().map(ProductDTO::new).toList();
    }

    public ProductDTO retrieveProductById(final int productId) throws ProductDoesNotExistException {
        final Product product = findProductById(productId).orElseThrow(
                () -> new ProductDoesNotExistException(productId));
        return new ProductDTO(product);
    }

    public void updateProductsStock(final List<PurchaseProductDTO> purchaseProductDTOS)
            throws ProductDoesNotExistException {

        for (final PurchaseProductDTO purchaseProductDTO : purchaseProductDTOS) {
            //Retrieve stock count for product id
            //TODO: Replace exception handling here with same logic above
            final Optional<Product> product = findProductById(purchaseProductDTO.productId());

            if (product.isPresent()) {
                final int newStockCountValue =
                        product.get().getStockCount() - purchaseProductDTO.quantity();
                //Update the stock of that product id
                log.info("Updating product {} stock value from {} to {}",
                        purchaseProductDTO.productId(), product.get().getStockCount(),
                        newStockCountValue);
                this.productRepository.updateStockCountByProductId(newStockCountValue,
                        purchaseProductDTO.productId());
                log.info("Product {} stock updated", purchaseProductDTO.productId());
            } else {
                throw new ProductDoesNotExistException(purchaseProductDTO.productId());
            }
        }
    }

    //TODO: This can probably be removed
    private Optional<Product> findProductById(final int productId) {
        return this.productRepository.findById(productId);
    }

    public List<ProductDTO> getProductsByIds(final List<Integer> productIds) {
        final List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            final List<Integer> missingIds = productIds.stream()
                    .filter(id -> products.stream().noneMatch(p -> p.getProductId().equals(id)))
                    .toList();
            throw new ProductDoesNotExistException(missingIds);
        }
        return products.stream().map(ProductDTO::new).toList();
    }
}
