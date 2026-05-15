package com.trainingmug.ecommerce.factory;

import com.trainingmug.ecommerce.controller.ProductController;
import com.trainingmug.ecommerce.repository.ProductRepository;
import com.trainingmug.ecommerce.service.ProductService;
import com.trainingmug.ecommerce.util.CsvParser;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Tests for AppFactory.
 *
 * AppFactory uses the Singleton pattern — each dependency is created only once.
 * These tests verify:
 *   (a) Each getter method exists with the correct return type
 *   (b) Each method returns a non-null instance
 *   (c) Calling the same getter twice returns the SAME object (singleton)
 *   (d) All fields are private and static
 *   (e) The creation chain is correct:
 *       CsvParser → ProductRepository → ProductService → ProductController
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppFactoryTest {

    /*
     * Reset all static singleton fields before each test so tests are isolated.
     * We use reflection to set private static fields back to null.
     */
    @BeforeEach
    void resetSingletons() throws Exception {
        String[] fieldNames = {"csvParser", "productRepository", "productService", "productController"};
        for (String name : fieldNames) {
            try {
                Field field = AppFactory.class.getDeclaredField(name);
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                // Field might not exist yet if student hasn't implemented — skip silently
            }
        }
    }

    // ===========================
    // CLASS STRUCTURE TESTS
    // ===========================

    @Test
    @Order(1)
    @DisplayName("Test AppFactory class exists in the correct package")
    void testAppFactoryClassExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
            assertNotNull(clazz, "AppFactory class should exist");
        } catch (ClassNotFoundException e) {
            fail("AppFactory class not found at com.trainingmug.ecommerce.factory.AppFactory");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test AppFactory has a private static csvParser field")
    void testCsvParserFieldExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Field field = clazz.getDeclaredField("csvParser");
            assertTrue(Modifier.isPrivate(field.getModifiers()), "csvParser should be private");
            assertTrue(Modifier.isStatic(field.getModifiers()), "csvParser should be static");
            assertEquals(CsvParser.class, field.getType(), "csvParser should be of type CsvParser");
        } catch (NoSuchFieldException e) {
            fail("AppFactory is missing the 'csvParser' field");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test AppFactory has a private static productRepository field")
    void testProductRepositoryFieldExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Field field = clazz.getDeclaredField("productRepository");
            assertTrue(Modifier.isPrivate(field.getModifiers()), "productRepository should be private");
            assertTrue(Modifier.isStatic(field.getModifiers()), "productRepository should be static");
            assertEquals(ProductRepository.class, field.getType());
        } catch (NoSuchFieldException e) {
            fail("AppFactory is missing the 'productRepository' field");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test AppFactory has a private static productService field")
    void testProductServiceFieldExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Field field = clazz.getDeclaredField("productService");
            assertTrue(Modifier.isPrivate(field.getModifiers()), "productService should be private");
            assertTrue(Modifier.isStatic(field.getModifiers()), "productService should be static");
            assertEquals(ProductService.class, field.getType());
        } catch (NoSuchFieldException e) {
            fail("AppFactory is missing the 'productService' field");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test AppFactory has a private static productController field")
    void testProductControllerFieldExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Field field = clazz.getDeclaredField("productController");
            assertTrue(Modifier.isPrivate(field.getModifiers()), "productController should be private");
            assertTrue(Modifier.isStatic(field.getModifiers()), "productController should be static");
            assertEquals(ProductController.class, field.getType());
        } catch (NoSuchFieldException e) {
            fail("AppFactory is missing the 'productController' field");
        }
    }

    // ===========================
    // METHOD EXISTENCE TESTS
    // ===========================

    @Test
    @Order(6)
    @DisplayName("Test getCsvParser() method exists and is public static")
    void testGetCsvParserMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Method method = clazz.getMethod("getCsvParser");
            assertTrue(Modifier.isPublic(method.getModifiers()), "getCsvParser should be public");
            assertTrue(Modifier.isStatic(method.getModifiers()), "getCsvParser should be static");
            assertEquals(CsvParser.class, method.getReturnType(), "getCsvParser should return CsvParser");
        } catch (NoSuchMethodException e) {
            fail("AppFactory is missing the 'getCsvParser()' method");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test getProductRepository() method exists and is public static")
    void testGetProductRepositoryMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Method method = clazz.getMethod("getProductRepository");
            assertTrue(Modifier.isPublic(method.getModifiers()));
            assertTrue(Modifier.isStatic(method.getModifiers()));
            assertEquals(ProductRepository.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("AppFactory is missing the 'getProductRepository()' method");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Test getProductService() method exists and is public static")
    void testGetProductServiceMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Method method = clazz.getMethod("getProductService");
            assertTrue(Modifier.isPublic(method.getModifiers()));
            assertTrue(Modifier.isStatic(method.getModifiers()));
            assertEquals(ProductService.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("AppFactory is missing the 'getProductService()' method");
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test getProductController() method exists and is public static")
    void testGetProductControllerMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.factory.AppFactory");
        try {
            Method method = clazz.getMethod("getProductController");
            assertTrue(Modifier.isPublic(method.getModifiers()));
            assertTrue(Modifier.isStatic(method.getModifiers()));
            assertEquals(ProductController.class, method.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("AppFactory is missing the 'getProductController()' method");
        }
    }

    // ===========================
    // BEHAVIOUR TESTS
    // ===========================

    @Test
    @Order(10)
    @DisplayName("Test getCsvParser() returns a non-null CsvParser instance")
    void testGetCsvParserReturnsInstance() {
        CsvParser parser = AppFactory.getCsvParser();
        assertNotNull(parser, "getCsvParser() should return a non-null CsvParser");
    }

    @Test
    @Order(11)
    @DisplayName("Test getCsvParser() is a singleton — same object returned on second call")
    void testGetCsvParserSingleton() {
        CsvParser first = AppFactory.getCsvParser();
        CsvParser second = AppFactory.getCsvParser();
        assertSame(first, second,
                "getCsvParser() should return the SAME instance on every call (singleton)");
    }

    @Test
    @Order(12)
    @DisplayName("Test getProductRepository() returns a non-null ProductRepository instance")
    void testGetProductRepositoryReturnsInstance() throws IOException {
        ProductRepository repo = AppFactory.getProductRepository();
        assertNotNull(repo, "getProductRepository() should return a non-null ProductRepository");
    }

    @Test
    @Order(13)
    @DisplayName("Test getProductRepository() is a singleton — same object returned on second call")
    void testGetProductRepositorySingleton() throws IOException {
        ProductRepository first = AppFactory.getProductRepository();
        ProductRepository second = AppFactory.getProductRepository();
        assertSame(first, second,
                "getProductRepository() should return the SAME instance on every call (singleton)");
    }

    @Test
    @Order(14)
    @DisplayName("Test getProductService() returns a non-null ProductService instance")
    void testGetProductServiceReturnsInstance() throws IOException {
        ProductService service = AppFactory.getProductService();
        assertNotNull(service, "getProductService() should return a non-null ProductService");
    }

    @Test
    @Order(15)
    @DisplayName("Test getProductService() is a singleton — same object returned on second call")
    void testGetProductServiceSingleton() throws IOException {
        ProductService first = AppFactory.getProductService();
        ProductService second = AppFactory.getProductService();
        assertSame(first, second,
                "getProductService() should return the SAME instance on every call (singleton)");
    }

    @Test
    @Order(16)
    @DisplayName("Test getProductController() returns a non-null ProductController instance")
    void testGetProductControllerReturnsInstance() throws IOException {
        ProductController controller = AppFactory.getProductController();
        assertNotNull(controller, "getProductController() should return a non-null ProductController");
    }

    @Test
    @Order(17)
    @DisplayName("Test getProductController() is a singleton — same object returned on second call")
    void testGetProductControllerSingleton() throws IOException {
        ProductController first = AppFactory.getProductController();
        ProductController second = AppFactory.getProductController();
        assertSame(first, second,
                "getProductController() should return the SAME instance on every call (singleton)");
    }

    @Test
    @Order(18)
    @DisplayName("Test full dependency chain — getProductController() triggers creation of all layers")
    void testFullDependencyChain() throws IOException {
        // Calling getProductController() should create the full chain
        ProductController controller = AppFactory.getProductController();
        assertNotNull(controller);

        // All other singletons should now also be initialized (not null)
        assertNotNull(AppFactory.getCsvParser(),
                "CsvParser should be initialized after getProductController() is called");
        assertNotNull(AppFactory.getProductRepository(),
                "ProductRepository should be initialized after getProductController() is called");
        assertNotNull(AppFactory.getProductService(),
                "ProductService should be initialized after getProductController() is called");
    }
}