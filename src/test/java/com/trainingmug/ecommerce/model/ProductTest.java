package com.trainingmug.ecommerce.model;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Tests for the Product model class.
 * Verifies that all required fields exist, Lombok annotations work correctly,
 * and the Builder pattern is functional.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductTest {

    @Test
    @Order(1)
    @DisplayName("Test Product class exists in correct package")
    void testProductClassExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.model.Product");
            assertNotNull(clazz, "Product class should exist");
        } catch (ClassNotFoundException e) {
            fail("Product class not found at com.trainingmug.ecommerce.model.Product");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test Product has all required fields")
    void testProductFields() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.model.Product");
        String[] expectedFields = {"id", "name", "maxRetailPrice", "discountPercentage",
                "rating", "isAvailable", "company", "category", "manufacturedYear"};

        for (String fieldName : expectedFields) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                assertNotNull(field, "Field '" + fieldName + "' should exist in Product");
            } catch (NoSuchFieldException e) {
                fail("Missing field in Product class: " + fieldName);
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test Product no-args constructor works (Lombok @NoArgsConstructor)")
    void testNoArgsConstructor() {
        assertDoesNotThrow(() -> {
            Product p = new Product();
            assertNotNull(p, "No-args constructor should create a Product instance");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Test Product all-args constructor works (Lombok @AllArgsConstructor)")
    void testAllArgsConstructor() {
        assertDoesNotThrow(() -> {
            Product p = new Product(1, "iPhone 15", 80000, 10.0f, 5, true, "Apple", "Electronics", 2023);
            assertNotNull(p);
            assertEquals(1, p.getId());
            assertEquals("iPhone 15", p.getName());
            assertEquals(80000, p.getMaxRetailPrice());
            assertEquals(10.0f, p.getDiscountPercentage());
            assertEquals(5, p.getRating());
            assertTrue(p.isAvailable());
            assertEquals("Apple", p.getCompany());
            assertEquals("Electronics", p.getCategory());
            assertEquals(2023, p.getManufacturedYear());
        });
    }

    @Test
    @Order(5)
    @DisplayName("Test Product Builder pattern works (Lombok @Builder)")
    void testBuilderPattern() {
        assertDoesNotThrow(() -> {
            Product p = Product.builder()
                    .id(2)
                    .name("MacBook Pro")
                    .maxRetailPrice(150000)
                    .discountPercentage(5.0f)
                    .isAvailable(true)
                    .company("Apple")
                    .category("Laptops")
                    .manufacturedYear(2023)
                    .build();

            assertNotNull(p);
            assertEquals(2, p.getId());
            assertEquals("MacBook Pro", p.getName());
            assertEquals(150000, p.getMaxRetailPrice());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Test Product getters and setters work (Lombok @Data)")
    void testGettersAndSetters() {
        Product p = new Product();
        p.setId(10);
        p.setName("Sony WH-1000XM5");
        p.setMaxRetailPrice(25000);
        p.setDiscountPercentage(20.0f);
        p.setAvailable(true);
        p.setCompany("Sony");
        p.setCategory("Audio");
        p.setManufacturedYear(2022);

        assertEquals(10, p.getId());
        assertEquals("Sony WH-1000XM5", p.getName());
        assertEquals(25000, p.getMaxRetailPrice());
        assertEquals(20.0f, p.getDiscountPercentage());
        assertTrue(p.isAvailable());
        assertEquals("Sony", p.getCompany());
        assertEquals("Audio", p.getCategory());
        assertEquals(2022, p.getManufacturedYear());
    }

    @Test
    @Order(7)
    @DisplayName("Test Product equals and hashCode (Lombok @Data)")
    void testEqualsAndHashCode() {
        Product p1 = Product.builder().id(1).name("iPhone").maxRetailPrice(80000).build();
        Product p2 = Product.builder().id(1).name("iPhone").maxRetailPrice(80000).build();
        Product p3 = Product.builder().id(2).name("Samsung").maxRetailPrice(70000).build();

        assertEquals(p1, p2, "Two products with same fields should be equal");
        assertNotEquals(p1, p3, "Two products with different fields should not be equal");
        assertEquals(p1.hashCode(), p2.hashCode(), "Equal products should have same hashCode");
    }

    @Test
    @Order(8)
    @DisplayName("Test Product toString is not null (Lombok @Data)")
    void testToString() {
        Product p = Product.builder().id(1).name("iPhone 15").build();
        assertNotNull(p.toString(), "toString should not return null");
        assertTrue(p.toString().contains("iPhone 15"), "toString should contain field values");
    }
}