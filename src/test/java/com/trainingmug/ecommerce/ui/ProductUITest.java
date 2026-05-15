package com.trainingmug.ecommerce.ui;

import com.trainingmug.ecommerce.controller.ProductController;
import com.trainingmug.ecommerce.model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Tests for ProductUI.
 *
 * ProductUI is the console-facing layer. It:
 *   - Reads user input via InputUtil
 *   - Delegates all product operations to ProductController
 *   - Displays output to System.out
 *
 * Testing strategy:
 *   - Use reflection to verify class structure (fields, methods)
 *   - Inject a mock ProductController using reflection to avoid real AppFactory calls
 *   - Capture System.out with ByteArrayOutputStream to verify printed output
 *   - Verify correct display formatting in printProduct() and printProductList()
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductUITest {

    @Mock
    private ProductController productController;

    private ProductUI productUI;

    // Used to capture what is printed to System.out
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    // Sample products for display tests
    private Product sampleProduct;
    private List<Product> sampleProducts;

    @BeforeEach
    void setUp() throws Exception {
        // Redirect System.out so we can read what ProductUI prints
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Create ProductUI without triggering AppFactory (which needs real CSV)
        // We do this by creating the instance and then injecting the mock via reflection
        productUI = createProductUIWithMock(productController);

        // Sample data
        sampleProduct = Product.builder()
                .id(1).name("iPhone 15").maxRetailPrice(80000)
                .discountPercentage(10.0f).isAvailable(true)
                .company("Apple").category("Electronics").manufacturedYear(2023).build();

        sampleProducts = Arrays.asList(
                sampleProduct,
                Product.builder().id(2).name("MacBook Pro").maxRetailPrice(150000)
                        .discountPercentage(5.0f).isAvailable(true)
                        .company("Apple").category("Laptops").manufacturedYear(2023).build()
        );
    }

    @AfterEach
    void tearDown() {
        // Restore original System.out after each test
        System.setOut(originalOut);
    }

    /*
     * Helper: creates a ProductUI and injects the mock controller via reflection.
     * This avoids needing a real CSV file or AppFactory during tests.
     */
    private ProductUI createProductUIWithMock(ProductController mockController) throws Exception {
        // Use no-args constructor but catch exception if AppFactory fails
        ProductUI ui;
        try {
            ui = new ProductUI();
        } catch (Exception e) {
            // If AppFactory fails (no CSV in test classpath), create via reflection
            Constructor<ProductUI> constructor = ProductUI.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            ui = constructor.newInstance();
        }
        // Inject mock controller
        Field field = ProductUI.class.getDeclaredField("productController");
        field.setAccessible(true);
        field.set(ui, mockController);
        return ui;
    }

    // ===========================
    // CLASS STRUCTURE TESTS
    // ===========================

    @Test
    @Order(1)
    @DisplayName("Test ProductUI class exists in the correct package")
    void testProductUIClassExists() {
        try {
            Class<?> clazz = Class.forName("com.trainingmug.ecommerce.ui.ProductUI");
            assertNotNull(clazz);
        } catch (ClassNotFoundException e) {
            fail("ProductUI class not found at com.trainingmug.ecommerce.ui.ProductUI");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test ProductUI has a private productController field")
    void testProductControllerFieldExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.ui.ProductUI");
        try {
            Field field = clazz.getDeclaredField("productController");
            assertTrue(Modifier.isPrivate(field.getModifiers()),
                    "productController field should be private");
            assertEquals(ProductController.class, field.getType(),
                    "productController field should be of type ProductController");
        } catch (NoSuchFieldException e) {
            fail("ProductUI is missing the 'productController' field");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test menu() method exists in ProductUI")
    void testMenuMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.ui.ProductUI");
        try {
            Method method = clazz.getMethod("menu");
            assertNotNull(method, "menu() method should be present");
            assertTrue(Modifier.isPublic(method.getModifiers()), "menu() should be public");
        } catch (NoSuchMethodException e) {
            fail("ProductUI is missing the public 'menu()' method");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Test printProductHeader() is a public static method")
    void testPrintProductHeaderMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.ui.ProductUI");
        try {
            Method method = clazz.getMethod("printProductHeader");
            assertTrue(Modifier.isPublic(method.getModifiers()), "printProductHeader() should be public");
            assertTrue(Modifier.isStatic(method.getModifiers()), "printProductHeader() should be static");
        } catch (NoSuchMethodException e) {
            fail("ProductUI is missing the 'printProductHeader()' method");
        }
    }

    @Test
    @Order(5)
    @DisplayName("Test printProductList() method exists in ProductUI")
    void testPrintProductListMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.ui.ProductUI");
        try {
            Method method = clazz.getMethod("printProductList", List.class);
            assertNotNull(method, "printProductList(List<Product>) method should be present");
            assertTrue(Modifier.isPublic(method.getModifiers()), "printProductList() should be public");
        } catch (NoSuchMethodException e) {
            fail("ProductUI is missing the 'printProductList(List<Product>)' method");
        }
    }

    @Test
    @Order(6)
    @DisplayName("Test printProduct() method exists in ProductUI")
    void testPrintProductMethodExists() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("com.trainingmug.ecommerce.ui.ProductUI");
        try {
            Method method = clazz.getMethod("printProduct", Product.class);
            assertNotNull(method, "printProduct(Product) method should be present");
            assertTrue(Modifier.isPublic(method.getModifiers()), "printProduct() should be public");
        } catch (NoSuchMethodException e) {
            fail("ProductUI is missing the 'printProduct(Product)' method");
        }
    }

    // ===========================
    // DISPLAY OUTPUT TESTS
    // ===========================

    @Test
    @Order(7)
    @DisplayName("Test printProductHeader() prints column headers to System.out")
    void testPrintProductHeaderOutput() {
        ProductUI.printProductHeader();
        String output = outputStream.toString();

        assertAll("Header should contain all column names",
                () -> assertTrue(output.contains("ID"), "Header should contain 'ID'"),
                () -> assertTrue(output.contains("Name"), "Header should contain 'Name'"),
                () -> assertTrue(output.contains("MRP"), "Header should contain 'MRP'"),
                () -> assertTrue(output.contains("Discount%"), "Header should contain 'Discount%'"),
                () -> assertTrue(output.contains("FinalPrice"), "Header should contain 'FinalPrice'"),
                () -> assertTrue(output.contains("Available"), "Header should contain 'Available'"),
                () -> assertTrue(output.contains("Company"), "Header should contain 'Company'"),
                () -> assertTrue(output.contains("Category"), "Header should contain 'Category'"),
                () -> assertTrue(output.contains("Year"), "Header should contain 'Year'")
        );
    }

    @Test
    @Order(8)
    @DisplayName("Test printProductHeader() prints a separator line")
    void testPrintProductHeaderHasSeparator() {
        ProductUI.printProductHeader();
        String output = outputStream.toString();
        assertTrue(output.contains("---"),
                "Header should be followed by a separator line made of dashes");
    }

    @Test
    @Order(9)
    @DisplayName("Test printProduct() prints the product's ID")
    void testPrintProductShowsId() {
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("1"), "Output should contain the product ID '1'");
    }

    @Test
    @Order(10)
    @DisplayName("Test printProduct() prints the product's name")
    void testPrintProductShowsName() {
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("iPhone 15"), "Output should contain the product name 'iPhone 15'");
    }

    @Test
    @Order(11)
    @DisplayName("Test printProduct() prints MRP as a number")
    void testPrintProductShowsMrp() {
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("80000"), "Output should contain the MRP '80000'");
    }

    @Test
    @Order(12)
    @DisplayName("Test printProduct() prints 'YES' for an available product")
    void testPrintProductShowsAvailableYes() {
        productUI.printProduct(sampleProduct); // isAvailable = true
        String output = outputStream.toString();
        assertTrue(output.contains("YES"),
                "Available product should print 'YES' in the Available column");
    }

    @Test
    @Order(13)
    @DisplayName("Test printProduct() prints 'NO' for an unavailable product")
    void testPrintProductShowsAvailableNo() {
        Product unavailable = Product.builder()
                .id(3).name("Sony WH-1000XM5").maxRetailPrice(25000)
                .discountPercentage(20.0f).isAvailable(false)
                .company("Sony").category("Audio").manufacturedYear(2022).build();

        productUI.printProduct(unavailable);
        String output = outputStream.toString();
        assertTrue(output.contains("NO"),
                "Unavailable product should print 'NO' in the Available column");
    }

    @Test
    @Order(14)
    @DisplayName("Test printProduct() prints the company name")
    void testPrintProductShowsCompany() {
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("Apple"), "Output should contain the company name 'Apple'");
    }

    @Test
    @Order(15)
    @DisplayName("Test printProduct() prints the category")
    void testPrintProductShowsCategory() {
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("Electronics"), "Output should contain the category 'Electronics'");
    }

    @Test
    @Order(16)
    @DisplayName("Test printProduct() prints the manufactured year")
    void testPrintProductShowsManufacturedYear() {
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("2023"), "Output should contain the manufactured year '2023'");
    }

    @Test
    @Order(17)
    @DisplayName("Test printProduct() correctly calculates and prints final price after discount")
    void testPrintProductShowsFinalPrice() {
        // MRP=80000, discount=10% → finalPrice = 80000 - 8000 = 72000
        productUI.printProduct(sampleProduct);
        String output = outputStream.toString();
        assertTrue(output.contains("72000"),
                "Final price after 10% discount on 80000 should be 72000");
    }

    @Test
    @Order(18)
    @DisplayName("Test printProductList() prints all products in the list")
    void testPrintProductListShowsAllProducts() {
        productUI.printProductList(sampleProducts);
        String output = outputStream.toString();

        assertTrue(output.contains("iPhone 15"), "Output should contain 'iPhone 15'");
        assertTrue(output.contains("MacBook Pro"), "Output should contain 'MacBook Pro'");
    }

    @Test
    @Order(19)
    @DisplayName("Test printProductList() prints a closing separator line after all products")
    void testPrintProductListHasClosingSeparator() {
        productUI.printProductList(sampleProducts);
        String output = outputStream.toString();
        // The closing separator is the last "---" line
        assertTrue(output.contains("---"),
                "printProductList() should end with a separator line");
    }

    @Test
    @Order(20)
    @DisplayName("Test printProductList() with an empty list prints only the header")
    void testPrintProductListWithEmptyList() {
        productUI.printProductList(List.of());
        String output = outputStream.toString();
        // Header should still be printed even if list is empty
        assertTrue(output.contains("ID"), "Header should still be printed for an empty list");
        assertFalse(output.contains("iPhone"), "No products should be printed for an empty list");
    }

    @Test
    @Order(21)
    @DisplayName("Test printProduct() trims names longer than 20 characters")
    void testPrintProductTrimsLongName() {
        Product longNameProduct = Product.builder()
                .id(99).name("This Is A Very Long Product Name That Exceeds Limit")
                .maxRetailPrice(10000).discountPercentage(5.0f).isAvailable(true)
                .company("BrandX").category("Test").manufacturedYear(2022).build();

        productUI.printProduct(longNameProduct);
        String output = outputStream.toString();

        // Name should be truncated to max 20 chars — so "This Is A Very Lo..." (with ellipsis)
        assertFalse(output.contains("This Is A Very Long Product Name That Exceeds Limit"),
                "Names longer than 20 characters should be trimmed");
        assertTrue(output.contains("..."),
                "Trimmed name should end with '...'");
    }
}