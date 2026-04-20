package com.glo.lending.product.exception;

import java.util.UUID;

/**
 * Thrown when a product with the given identifier cannot be found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final UUID productId) {
        super("Product not found with id: " + productId);
    }
}

