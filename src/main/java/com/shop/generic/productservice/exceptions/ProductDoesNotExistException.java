package com.shop.generic.productservice.exceptions;

import java.util.List;
import lombok.Getter;

@Getter
public class ProductDoesNotExistException extends RuntimeException {


    public ProductDoesNotExistException(final int productId) {
        super("No product with Product ID " + productId + " exists");
    }

    public ProductDoesNotExistException(final List<Integer> productIds) {
        super("Request could not be completed because products with the following IDs do not exist: "
                + productIds);
    }
}
