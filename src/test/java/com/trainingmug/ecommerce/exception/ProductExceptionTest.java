package com.trainingmug.ecommerce.exception;

import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Tests for custom exception classes.
 * Verifies that both exceptions extend RuntimeException and accept a String message.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductExceptionTest {

    // ===================================================
    // ProductExistsException Tests
    // ===================================================

    @Test
    @Order(1)
    @DisplayName("Test ProductExistsException class exists in correct package")
    void testProductExistsExceptionClassExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.exception.ProductExistsException");
            assertNotNull(clazz);
        } catch (ClassNotFoundException e) {
            fail("ProductExistsException not found at com.trainingmug.ecommerce.exception.ProductExistsException");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test ProductExistsException extends RuntimeException")
    void testProductExistsExceptionExtendsRuntimeException() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.exception.ProductExistsException");
        assertTrue(
                RuntimeException.class.isAssignableFrom(clazz),
                "ProductExistsException must extend RuntimeException"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Test ProductExistsException has String constructor")
    void testProductExistsExceptionConstructor() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.exception.ProductExistsException");
            Constructor<?> constructor = clazz.getConstructor(String.class);
            assertNotNull(constructor, "Constructor with String argument should exist");
        } catch (ClassNotFoundException e) {
            fail("ProductExistsException class not found");
        } catch (NoSuchMethodException e) {
            fail("ProductExistsException must have a constructor that accepts a String message");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test ProductExistsException stores and returns the message correctly")
    void testProductExistsExceptionMessage() {
        String expectedMessage = "Product already exists with id : 5";
        ProductExistsException ex = new ProductExistsException(expectedMessage);
        assertEquals(expectedMessage, ex.getMessage(),
                "Exception message should match the one passed to the constructor");
    }

    @Test
    @Order(5)
    @DisplayName("Test ProductExistsException is thrown correctly")
    void testProductExistsExceptionIsThrown() {
        assertThrows(ProductExistsException.class, () -> {
            throw new ProductExistsException("Product already exists with id : 1");
        });
    }

    // ===================================================
    // ProductNotFoundException Tests
    // ===================================================

    @Test
    @Order(6)
    @DisplayName("Test ProductNotFoundException class exists in correct package")
    void testProductNotFoundExceptionClassExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.exception.ProductNotFoundException");
            assertNotNull(clazz);
        } catch (ClassNotFoundException e) {
            fail("ProductNotFoundException not found at com.trainingmug.ecommerce.exception.ProductNotFoundException");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test ProductNotFoundException extends RuntimeException")
    void testProductNotFoundExceptionExtendsRuntimeException() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.exception.ProductNotFoundException");
        assertTrue(
                RuntimeException.class.isAssignableFrom(clazz),
                "ProductNotFoundException must extend RuntimeException"
        );
    }

    @Test
    @Order(8)
    @DisplayName("Test ProductNotFoundException has String constructor")
    void testProductNotFoundExceptionConstructor() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.exception.ProductNotFoundException");
            Constructor<?> constructor = clazz.getConstructor(String.class);
            assertNotNull(constructor, "Constructor with String argument should exist");
        } catch (ClassNotFoundException e) {
            fail("ProductNotFoundException class not found");
        } catch (NoSuchMethodException e) {
            fail("ProductNotFoundException must have a constructor that accepts a String message");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test ProductNotFoundException stores and returns the message correctly")
    void testProductNotFoundExceptionMessage() {
        String expectedMessage = "Product with id : 99 not found!";
        ProductNotFoundException ex = new ProductNotFoundException(expectedMessage);
        assertEquals(expectedMessage, ex.getMessage(),
                "Exception message should match the one passed to the constructor");
    }

    @Test
    @Order(10)
    @DisplayName("Test ProductNotFoundException is thrown correctly")
    void testProductNotFoundExceptionIsThrown() {
        assertThrows(ProductNotFoundException.class, () -> {
            throw new ProductNotFoundException("Product with id : 99 not found!");
        });
    }
}