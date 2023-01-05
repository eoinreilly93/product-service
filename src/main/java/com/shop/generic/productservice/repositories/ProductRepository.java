package com.shop.generic.productservice.repositories;

import com.shop.generic.common.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Modifying
    @Query("update Product p set p.stockCount = ?1 where p.productId = ?2")
    int updateStockCountByProductId(Integer stockCount, Integer productId);

}
