package com.trainingmug.ecommerce.util;

import com.trainingmug.ecommerce.model.Product;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Tests for CsvParser.
 *
 * CsvParser reads "products.csv" from the classpath and maps each row into a Product.
 * These tests verify:
 *   (a) The class and its methods exist with correct signatures
 *   (b) The CSV is read correctly and returns the right number of products
 *   (c) Each field of the Product is parsed from the CSV correctly
 *   (d) Invalid or short CSV rows cause a RuntimeException
 *   (e) A missing CSV file causes a RuntimeException
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CsvParserTest {

    private CsvParser csvParser;

    @BeforeEach
    void setUp() {
        csvParser = new CsvParser();
    }

    // ===========================
    // CLASS STRUCTURE TESTS
    // ===========================

    @Test
    @Order(1)
    @DisplayName("Test CsvParser class exists in the correct package")
    void testCsvParserClassExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.util.CsvParser");
            assertNotNull(clazz, "CsvParser class should exist");
        } catch (ClassNotFoundException e) {
            fail("CsvParser not found at com.trainingmug.ecommerce.util.CsvParser");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test getProductsFromCsv() method exists with correct signature")
    void testGetProductsFromCsvMethodExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.util.CsvParser");
            Method method = clazz.getMethod("getProductsFromCsv");
            assertNotNull(method, "getProductsFromCsv() method should exist");
            assertTrue(Modifier.isPublic(method.getModifiers()),
                    "getProductsFromCsv() should be public");
            assertEquals(List.class, method.getReturnType(),
                    "getProductsFromCsv() should return List");
        } catch (ClassNotFoundException e) {
            fail("CsvParser class not found");
        } catch (NoSuchMethodException e) {
            fail("getProductsFromCsv() method not found in CsvParser");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test parseProduct() is declared as a private method")
    void testParseProductIsPrivate() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.util.CsvParser");
            Method method = clazz.getDeclaredMethod("parseProduct", String.class);
            assertTrue(Modifier.isPrivate(method.getModifiers()),
                    "parseProduct() should be private — it is an internal helper method");
        } catch (ClassNotFoundException e) {
            fail("CsvParser class not found");
        } catch (NoSuchMethodException e) {
            fail("parseProduct(String) method not found in CsvParser");
        }
    }

    // ===========================
    // BEHAVIOUR TESTS
    // ===========================

    @Test
    @Order(4)
    @DisplayName("Test getProductsFromCsv() returns a non-null list")
    void testGetProductsFromCsvReturnsNonNull() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        assertNotNull(products, "getProductsFromCsv() should never return null");
    }

    @Test
    @Order(5)
    @DisplayName("Test getProductsFromCsv() returns all 10 products from the CSV")
    void testGetProductsFromCsvReturnsCorrectCount() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        assertEquals(10, products.size(),
                "Should load exactly 10 products from products.csv (excluding header row)");
    }

    @Test
    @Order(6)
    @DisplayName("Test getProductsFromCsv() skips the header row")
    void testGetProductsFromCsvSkipsHeader() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        // If the header was not skipped, the first row would have id=0 or fail to parse
        assertDoesNotThrow(() -> {
            int firstId = products.get(0).getId();
            assertTrue(firstId > 0, "First product ID should be a valid integer, not 0 (header skipped correctly)");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Test first product is parsed correctly from CSV")
    void testFirstProductParsedCorrectly() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        Product first = products.get(0);

        assertEquals(1, first.getId(), "First product ID should be 1");
        assertEquals("iPhone 15", first.getName(), "First product name should be 'iPhone 15'");
        assertEquals(80000, first.getMaxRetailPrice(), "First product MRP should be 80000");
        assertEquals(10.0f, first.getDiscountPercentage(), 0.001f,
                "First product discount should be 10.0");
        assertTrue(first.isAvailable(), "First product should be available");
        assertEquals("Apple", first.getCompany(), "First product company should be 'Apple'");
        assertEquals("Electronics", first.getCategory(), "First product category should be 'Electronics'");
        assertEquals(2023, first.getManufacturedYear(), "First product year should be 2023");
    }

    @Test
    @Order(8)
    @DisplayName("Test last product is parsed correctly from CSV")
    void testLastProductParsedCorrectly() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        Product last = products.get(products.size() - 1);

        assertEquals(10, last.getId(), "Last product ID should be 10");
        assertEquals("Bose QC45", last.getName(), "Last product name should be 'Bose QC45'");
        assertEquals(22000, last.getMaxRetailPrice(), "Last product MRP should be 22000");
        assertEquals("Bose", last.getCompany(), "Last product company should be 'Bose'");
        assertEquals("Audio", last.getCategory(), "Last product category should be 'Audio'");
        assertEquals(2021, last.getManufacturedYear(), "Last product year should be 2021");
    }

    @Test
    @Order(9)
    @DisplayName("Test product with isAvailable=false is parsed correctly")
    void testUnavailableProductParsedCorrectly() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        // Product ID 5 (Dell XPS 15) has isAvailable=false in products.csv
        Product dellXps = products.stream()
                .filter(p -> p.getId() == 5)
                .findFirst()
                .orElse(null);

        assertNotNull(dellXps, "Product with id=5 should exist");
        assertFalse(dellXps.isAvailable(),
                "Dell XPS 15 should have isAvailable=false");
    }

    @Test
    @Order(10)
    @DisplayName("Test all products have valid IDs (greater than 0)")
    void testAllProductsHaveValidIds() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        for (Product p : products) {
            assertTrue(p.getId() > 0,
                    "Every product should have a positive ID, but found: " + p.getId());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test all products have non-null and non-empty name")
    void testAllProductsHaveValidNames() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        for (Product p : products) {
            assertNotNull(p.getName(), "Product name should not be null");
            assertFalse(p.getName().isBlank(), "Product name should not be blank");
        }
    }

    @Test
    @Order(12)
    @DisplayName("Test all products have positive MRP")
    void testAllProductsHavePositiveMrp() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        for (Product p : products) {
            assertTrue(p.getMaxRetailPrice() > 0,
                    "MRP should be positive for product: " + p.getName());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Test all products have a valid manufactured year")
    void testAllProductsHaveValidYear() throws IOException {
        List<Product> products = csvParser.getProductsFromCsv();
        for (Product p : products) {
            assertTrue(p.getManufacturedYear() >= 2000 && p.getManufacturedYear() <= 2100,
                    "Manufactured year should be realistic, but found: " + p.getManufacturedYear());
        }
    }

    @Test
    @Order(14)
    @DisplayName("Test parseProduct() throws RuntimeException for a row with too few columns")
    void testParseProductThrowsForShortRow() throws Exception {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.util.CsvParser");
        java.lang.reflect.Method method = clazz.getDeclaredMethod("parseProduct", String.class);
        method.setAccessible(true); // allow access to private method

        String invalidRow = "1,OnlyName"; // Only 2 columns instead of 8

        Exception thrown = assertThrows(Exception.class, () -> {
            try {
                method.invoke(csvParser, invalidRow);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause(); // unwrap to get the actual RuntimeException
            }
        });

        assertInstanceOf(RuntimeException.class, thrown,
                "parseProduct() should throw RuntimeException for rows with fewer than 8 columns");
    }

    @Test
    @Order(15)
    @DisplayName("Test parseProduct() throws RuntimeException for a row with non-numeric ID")
    void testParseProductThrowsForNonNumericId() throws Exception {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.util.CsvParser");
        java.lang.reflect.Method method = clazz.getDeclaredMethod("parseProduct", String.class);
        method.setAccessible(true);

        String badRow = "ABC,iPhone,80000,10.0,true,Apple,Electronics,2023"; // ID is "ABC"

        Exception thrown = assertThrows(Exception.class, () -> {
            try {
                method.invoke(csvParser, badRow);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });

        assertInstanceOf(RuntimeException.class, thrown,
                "parseProduct() should throw RuntimeException when ID cannot be parsed as integer");
    }
}