package com.shop.generic.productservice.services;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.productservice.exceptions.NoProductException;
import com.shop.generic.productservice.repositories.ProductRepository;
import java.util.List;
import java.util.Optional;
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

    public ProductVO findProductById(final int productId) {
        final Product product = this.productRepository.findById(productId).orElse(null);
        return new ProductVO(product);
    }

    public void updateProductsStock(final List<PurchaseProductVO> purchaseProductVOS)
            throws NoProductException {

        //TODO: This can probably be optimised better on the SQL side
        for (final PurchaseProductVO purchaseProductVO : purchaseProductVOS) {
            //Retrieve stock count for product id
            final Optional<Product> product = this.productRepository.findById(
                    purchaseProductVO.productId());

            if (product.isPresent()) {
                final int newStockCountValue =
                        product.get().getStockCount() - purchaseProductVO.quantity();
                //Update the stock of that product id
                this.productRepository.updateStockCountByProductId(newStockCountValue,
                        purchaseProductVO.productId());
            } else {
                throw new NoProductException(purchaseProductVO.productId());
            }
        }
    }
}
