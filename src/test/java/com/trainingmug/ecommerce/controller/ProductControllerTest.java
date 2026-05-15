package com.trainingmug.ecommerce.controller;

import com.trainingmug.ecommerce.exception.ProductExistsException;
import com.trainingmug.ecommerce.exception.ProductNotFoundException;
import com.trainingmug.ecommerce.model.Product;
import com.trainingmug.ecommerce.service.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Tests for ProductController.
 *
 * The controller should ONLY delegate to ProductService — it has no logic of its own.
 * So each test verifies:
 *   (a) the controller calls the correct service method
 *   (b) the controller returns exactly what the service returns
 *
 * Pattern:
 *   @Mock ProductService   → fake service that we control
 *   @InjectMocks ProductController → real controller with mock injected
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product sampleProduct;
    private List<Product> sampleProducts;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1).name("iPhone 15").maxRetailPrice(80000)
                .discountPercentage(10.0f).isAvailable(true)
                .company("Apple").category("Electronics").manufacturedYear(2023).build();

        sampleProducts = Arrays.asList(
                sampleProduct,
                Product.builder().id(2).name("MacBook Pro").maxRetailPrice(150000)
                        .discountPercentage(5.0f).isAvailable(true)
                        .company("Apple").category("Laptops").manufacturedYear(2023).build(),
                Product.builder().id(3).name("Sony WH-1000XM5").maxRetailPrice(25000)
                        .discountPercentage(20.0f).isAvailable(false)
                        .company("Sony").category("Audio").manufacturedYear(2021).build()
        );
    }

    // ===========================
    // CRUD DELEGATION TESTS
    // ===========================

    @Test
    @Order(1)
    @DisplayName("Test save - delegates to productService.save() and returns result")
    void testSave() throws ProductExistsException {
        when(productService.save(sampleProduct)).thenReturn(sampleProduct);

        Product result = productController.save(sampleProduct);

        assertNotNull(result);
        assertEquals(sampleProduct, result);
        verify(productService, times(1)).save(sampleProduct);
    }

    @Test
    @Order(2)
    @DisplayName("Test save - propagates ProductExistsException from service")
    void testSavePropagatesException() throws ProductExistsException {
        when(productService.save(sampleProduct))
                .thenThrow(new ProductExistsException("Product already exists with id : 1"));

        assertThrows(ProductExistsException.class, () -> productController.save(sampleProduct));
    }

    @Test
    @Order(3)
    @DisplayName("Test getById - delegates to productService.getById() and returns product")
    void testGetById() throws ProductNotFoundException {
        when(productService.getById(1)).thenReturn(sampleProduct);

        Product result = productController.getById(1);

        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
        verify(productService, times(1)).getById(1);
    }

    @Test
    @Order(4)
    @DisplayName("Test getById - propagates ProductNotFoundException from service")
    void testGetByIdPropagatesException() throws ProductNotFoundException {
        when(productService.getById(99))
                .thenThrow(new ProductNotFoundException("Product with id : 99 not found!"));

        assertThrows(ProductNotFoundException.class, () -> productController.getById(99));
    }

    @Test
    @Order(5)
    @DisplayName("Test getAll - delegates to productService.getAll() and returns all products")
    void testGetAll() {
        when(productService.getAll()).thenReturn(sampleProducts);

        List<Product> result = productController.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(productService, times(1)).getAll();
    }

    @Test
    @Order(6)
    @DisplayName("Test update - delegates to productService.update() and returns updated product")
    void testUpdate() throws ProductNotFoundException {
        Product updated = Product.builder().id(1).name("iPhone 15 Pro").maxRetailPrice(90000).build();
        when(productService.update(1, updated)).thenReturn(updated);

        Product result = productController.update(1, updated);

        assertNotNull(result);
        assertEquals("iPhone 15 Pro", result.getName());
        verify(productService, times(1)).update(1, updated);
    }

    @Test
    @Order(7)
    @DisplayName("Test delete - delegates to productService.delete()")
    void testDelete() throws ProductNotFoundException {
        doNothing().when(productService).delete(1);

        assertDoesNotThrow(() -> productController.delete(1));
        verify(productService, times(1)).delete(1);
    }

    @Test
    @Order(8)
    @DisplayName("Test delete - propagates ProductNotFoundException from service")
    void testDeletePropagatesException() throws ProductNotFoundException {
        doThrow(new ProductNotFoundException("Product Not found with id: 99"))
                .when(productService).delete(99);

        assertThrows(ProductNotFoundException.class, () -> productController.delete(99));
    }

    // ===========================
    // FILTER DELEGATION TESTS
    // ===========================

    @Test
    @Order(9)
    @DisplayName("Test getProductsByAvailability - delegates to service and returns result")
    void testGetProductsByAvailability() {
        List<Product> available = List.of(sampleProducts.get(0), sampleProducts.get(1));
        when(productService.getProductsByAvailability(true)).thenReturn(available);

        List<Product> result = productController.getProductsByAvailability(true);

        assertEquals(2, result.size());
        verify(productService, times(1)).getProductsByAvailability(true);
    }

    @Test
    @Order(10)
    @DisplayName("Test getProductsByCategory - delegates to service")
    void testGetProductsByCategory() {
        List<Product> electronics = List.of(sampleProducts.get(0));
        when(productService.getProductsByCategory("Electronics")).thenReturn(electronics);

        List<Product> result = productController.getProductsByCategory("Electronics");

        assertEquals(1, result.size());
        verify(productService, times(1)).getProductsByCategory("Electronics");
    }

    @Test
    @Order(11)
    @DisplayName("Test getProductsByPriceGreaterThan - delegates to service")
    void testGetProductsByPriceGreaterThan() {
        when(productService.getProductsByPriceGreaterThan(50000))
                .thenReturn(List.of(sampleProducts.get(0), sampleProducts.get(1)));

        List<Product> result = productController.getProductsByPriceGreaterThan(50000);
        assertEquals(2, result.size());
        verify(productService, times(1)).getProductsByPriceGreaterThan(50000);
    }

    @Test
    @Order(12)
    @DisplayName("Test getProductsByPriceLessThan - delegates to service")
    void testGetProductsByPriceLessThan() {
        when(productService.getProductsByPriceLessThan(30000))
                .thenReturn(List.of(sampleProducts.get(2)));

        List<Product> result = productController.getProductsByPriceLessThan(30000);
        assertEquals(1, result.size());
        verify(productService, times(1)).getProductsByPriceLessThan(30000);
    }

    @Test
    @Order(13)
    @DisplayName("Test getProductsAfterYear - delegates to service")
    void testGetProductsAfterYear() {
        when(productService.getProductsAfterYear(2022))
                .thenReturn(List.of(sampleProducts.get(0), sampleProducts.get(1)));

        List<Product> result = productController.getProductsAfterYear(2022);
        assertEquals(2, result.size());
        verify(productService, times(1)).getProductsAfterYear(2022);
    }

    @Test
    @Order(14)
    @DisplayName("Test getAvailableProductsAbovePrice - delegates to service")
    void testGetAvailableProductsAbovePrice() {
        when(productService.getAvailableProductsAbovePrice(70000.0))
                .thenReturn(List.of(sampleProducts.get(0), sampleProducts.get(1)));

        List<Product> result = productController.getAvailableProductsAbovePrice(70000.0);
        assertEquals(2, result.size());
        verify(productService, times(1)).getAvailableProductsAbovePrice(70000.0);
    }

    // ===========================
    // BASIC INFO DELEGATION TESTS
    // ===========================

    @Test
    @Order(15)
    @DisplayName("Test getAllProductNames - delegates to service")
    void testGetAllProductNames() {
        List<String> names = List.of("iPhone 15", "MacBook Pro", "Sony WH-1000XM5");
        when(productService.getAllProductNames()).thenReturn(names);

        List<String> result = productController.getAllProductNames();
        assertEquals(3, result.size());
        verify(productService, times(1)).getAllProductNames();
    }

    @Test
    @Order(16)
    @DisplayName("Test countProductsBasedOnAvailability - delegates to service")
    void testCountProductsBasedOnAvailability() {
        when(productService.countProductsBasedOnAvailability(true)).thenReturn(2L);

        long count = productController.countProductsBasedOnAvailability(true);
        assertEquals(2L, count);
        verify(productService, times(1)).countProductsBasedOnAvailability(true);
    }

    @Test
    @Order(17)
    @DisplayName("Test hasProductFromCompany - delegates to service")
    void testHasProductFromCompany() {
        when(productService.hasProductFromCompany("Apple")).thenReturn(true);

        assertTrue(productController.hasProductFromCompany("Apple"));
        verify(productService, times(1)).hasProductFromCompany("Apple");
    }

    @Test
    @Order(18)
    @DisplayName("Test areAllProductsAvailable - delegates to service")
    void testAreAllProductsAvailable() {
        when(productService.areAllProductsAvailable()).thenReturn(false);

        assertFalse(productController.areAllProductsAvailable());
        verify(productService, times(1)).areAllProductsAvailable();
    }

    @Test
    @Order(19)
    @DisplayName("Test findFirstProduct - delegates to service and returns Optional")
    void testFindFirstProduct() {
        when(productService.findFirstProduct()).thenReturn(Optional.of(sampleProduct));

        Optional<Product> result = productController.findFirstProduct();
        assertTrue(result.isPresent());
        assertEquals("iPhone 15", result.get().getName());
        verify(productService, times(1)).findFirstProduct();
    }

    @Test
    @Order(20)
    @DisplayName("Test getUniqueCategories - delegates to service")
    void testGetUniqueCategories() {
        List<String> categories = List.of("Electronics", "Laptops", "Audio");
        when(productService.getUniqueCategories()).thenReturn(categories);

        List<String> result = productController.getUniqueCategories();
        assertEquals(3, result.size());
        verify(productService, times(1)).getUniqueCategories();
    }

    // ===========================
    // SORTING & TOP N TESTS
    // ===========================

    @Test
    @Order(21)
    @DisplayName("Test getTopNExpensiveProducts - delegates to service")
    void testGetTopNExpensiveProducts() {
        when(productService.getTopNExpensiveProducts(2))
                .thenReturn(List.of(sampleProducts.get(1), sampleProducts.get(0)));

        List<Product> result = productController.getTopNExpensiveProducts(2);
        assertEquals(2, result.size());
        verify(productService, times(1)).getTopNExpensiveProducts(2);
    }

    @Test
    @Order(22)
    @DisplayName("Test sortProductsByPriceAsc - delegates to service")
    void testSortProductsByPriceAsc() {
        when(productService.sortProductsByPriceAsc()).thenReturn(sampleProducts);

        List<Product> result = productController.sortProductsByPriceAsc();
        assertEquals(3, result.size());
        verify(productService, times(1)).sortProductsByPriceAsc();
    }

    @Test
    @Order(23)
    @DisplayName("Test sortProductsByNameDesc - delegates to service")
    void testSortProductsByNameDesc() {
        when(productService.sortProductsByNameDesc()).thenReturn(sampleProducts);

        List<Product> result = productController.sortProductsByNameDesc();
        assertEquals(3, result.size());
        verify(productService, times(1)).sortProductsByNameDesc();
    }

    // ===========================
    // AGGREGATION DELEGATION TESTS
    // ===========================

    @Test
    @Order(24)
    @DisplayName("Test getTotalInventoryValue - delegates to service")
    void testGetTotalInventoryValue() {
        when(productService.getTotalInventoryValue()).thenReturn(390000);

        Integer result = productController.getTotalInventoryValue();
        assertEquals(390000, result);
        verify(productService, times(1)).getTotalInventoryValue();
    }

    @Test
    @Order(25)
    @DisplayName("Test getTotalDiscountedValue - delegates to service")
    void testGetTotalDiscountedValue() {
        when(productService.getTotalDiscountedValue()).thenReturn(33100.0);

        double result = productController.getTotalDiscountedValue();
        assertEquals(33100.0, result, 0.01);
        verify(productService, times(1)).getTotalDiscountedValue();
    }

    // ===========================
    // GROUPING DELEGATION TESTS
    // ===========================

    @Test
    @Order(26)
    @DisplayName("Test countProductsByCategory - delegates to service")
    void testCountProductsByCategory() {
        Map<String, Long> counts = Map.of("Electronics", 1L, "Laptops", 2L);
        when(productService.countProductsByCategory()).thenReturn(counts);

        Map<String, Long> result = productController.countProductsByCategory();
        assertEquals(2, result.size());
        verify(productService, times(1)).countProductsByCategory();
    }

    @Test
    @Order(27)
    @DisplayName("Test groupProductsByCategory - delegates to service")
    void testGroupProductsByCategory() {
        Map<String, List<Product>> grouped = Map.of("Electronics", List.of(sampleProduct));
        when(productService.groupProductsByCategory()).thenReturn(grouped);

        Map<String, List<Product>> result = productController.groupProductsByCategory();
        assertNotNull(result);
        verify(productService, times(1)).groupProductsByCategory();
    }

    @Test
    @Order(28)
    @DisplayName("Test groupProductsByCompany - delegates to service")
    void testGroupProductsByCompany() {
        Map<String, List<Product>> grouped = Map.of("Apple", List.of(sampleProduct));
        when(productService.groupProductsByCompany()).thenReturn(grouped);

        Map<String, List<Product>> result = productController.groupProductsByCompany();
        assertNotNull(result);
        verify(productService, times(1)).groupProductsByCompany();
    }

    @Test
    @Order(29)
    @DisplayName("Test partitionByAvailability - delegates to service")
    void testPartitionByAvailability() {
        Map<Boolean, List<Product>> partitioned = Map.of(
                true, List.of(sampleProducts.get(0), sampleProducts.get(1)),
                false, List.of(sampleProducts.get(2))
        );
        when(productService.partitionByAvailability()).thenReturn(partitioned);

        Map<Boolean, List<Product>> result = productController.partitionByAvailability();
        assertNotNull(result);
        verify(productService, times(1)).partitionByAvailability();
    }

    // ===========================
    // MIN / MAX DELEGATION TESTS
    // ===========================

    @Test
    @Order(30)
    @DisplayName("Test getMaxPricedProduct - delegates to service")
    void testGetMaxPricedProduct() throws ProductNotFoundException {
        when(productService.getMaxPricedProduct()).thenReturn(sampleProducts.get(1)); // MacBook

        Product result = productController.getMaxPricedProduct();
        assertEquals("MacBook Pro", result.getName());
        verify(productService, times(1)).getMaxPricedProduct();
    }

    @Test
    @Order(31)
    @DisplayName("Test getMinPricedProduct - delegates to service")
    void testGetMinPricedProduct() throws ProductNotFoundException {
        when(productService.getMinPricedProduct()).thenReturn(sampleProducts.get(2)); // Sony

        Product result = productController.getMinPricedProduct();
        assertEquals("Sony WH-1000XM5", result.getName());
        verify(productService, times(1)).getMinPricedProduct();
    }

    // ===========================
    // MAP OPERATION DELEGATION TESTS
    // ===========================

    @Test
    @Order(32)
    @DisplayName("Test getProductMapById - delegates to service")
    void testGetProductMapById() {
        Map<Integer, Product> map = Map.of(1, sampleProduct);
        when(productService.getProductMapById()).thenReturn(map);

        Map<Integer, Product> result = productController.getProductMapById();
        assertNotNull(result);
        assertEquals("iPhone 15", result.get(1).getName());
        verify(productService, times(1)).getProductMapById();
    }

    @Test
    @Order(33)
    @DisplayName("Test getAveragePriceByCategory - delegates to service")
    void testGetAveragePriceByCategory() {
        Map<String, Double> avgMap = Map.of("Electronics", 80000.0, "Laptops", 135000.0);
        when(productService.getAveragePriceByCategory()).thenReturn(avgMap);

        Map<String, Double> result = productController.getAveragePriceByCategory();
        assertNotNull(result);
        assertEquals(80000.0, result.get("Electronics"), 0.01);
        verify(productService, times(1)).getAveragePriceByCategory();
    }

    @Test
    @Order(34)
    @DisplayName("Test getTop3ProductsByCategory - delegates to service")
    void testGetTop3ProductsByCategory() {
        Map<String, List<Product>> top3 = Map.of("Laptops", List.of(sampleProducts.get(1)));
        when(productService.getTop3ProductsByCategory()).thenReturn(top3);

        Map<String, List<Product>> result = productController.getTop3ProductsByCategory();
        assertNotNull(result);
        verify(productService, times(1)).getTop3ProductsByCategory();
    }
}