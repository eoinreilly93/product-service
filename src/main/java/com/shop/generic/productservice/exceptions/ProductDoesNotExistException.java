package com.shop.generic.productservice.exceptions;

public class ProductDoesNotExistException extends Exception {

    public ProductDoesNotExistException(final int productId) {
        super("No product with Product ID " + productId + " exists");
    }
}
