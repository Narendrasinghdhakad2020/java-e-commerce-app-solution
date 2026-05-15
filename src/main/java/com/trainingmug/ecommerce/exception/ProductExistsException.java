package com.trainingmug.ecommerce.exception;

public class ProductExistsException extends RuntimeException {
    public ProductExistsException(String msg) {
        super(msg);
    }

}
