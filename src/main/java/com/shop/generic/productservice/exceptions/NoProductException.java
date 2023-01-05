package com.shop.generic.productservice.exceptions;

public class NoProductException extends Exception {

    public NoProductException(final int productId) {
        super("No product with Product ID " + productId + " exists");
    }
}
