package com.trainingmug.ecommerce.service;

import com.trainingmug.ecommerce.exception.ProductExistsException;
import com.trainingmug.ecommerce.exception.ProductNotFoundException;
import com.trainingmug.ecommerce.model.Product;
import com.trainingmug.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Tests for ProductServiceImpl.
 *
 * Pattern used: Mockito with @Mock and @InjectMocks
 *  - @Mock ProductRepository  → a fake repository; we control what it returns
 *  - @InjectMocks ProductServiceImpl → the real service with the mock injected
 *
 * This isolates the service layer — no real CSV file or repository needed.
 *
 * Each when(...).thenReturn(...) line sets up what the mock will return when called.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    // Shared sample products used across tests
    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = Arrays.asList(
                Product.builder().id(1).name("iPhone 15").maxRetailPrice(80000)
                        .discountPercentage(10.0f).isAvailable(true)
                        .company("Apple").category("Electronics").manufacturedYear(2023).build(),
                Product.builder().id(2).name("MacBook Pro").maxRetailPrice(150000)
                        .discountPercentage(5.0f).isAvailable(true)
                        .company("Apple").category("Laptops").manufacturedYear(2023).build(),
                Product.builder().id(3).name("Sony WH-1000XM5").maxRetailPrice(25000)
                        .discountPercentage(20.0f).isAvailable(false)
                        .company("Sony").category("Audio").manufacturedYear(2021).build(),
                Product.builder().id(4).name("Dell XPS 15").maxRetailPrice(120000)
                        .discountPercentage(8.0f).isAvailable(false)
                        .company("Dell").category("Laptops").manufacturedYear(2022).build(),
                Product.builder().id(5).name("Adidas Ultraboost").maxRetailPrice(15000)
                        .discountPercentage(20.0f).isAvailable(true)
                        .company("Adidas").category("Footwear").manufacturedYear(2022).build()
        );
    }

    // ===========================
    // CRUD TESTS
    // ===========================

    @Test
    @Order(1)
    @DisplayName("Test save - saves a new product successfully")
    void testSaveSuccess() throws ProductExistsException {
        Product newProduct = Product.builder().id(10).name("OnePlus 11")
                .maxRetailPrice(60000).build();

        when(productRepository.findById(10)).thenReturn(Optional.empty());
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        Product saved = productService.save(newProduct);

        assertNotNull(saved);
        assertEquals(10, saved.getId());
        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    @Order(2)
    @DisplayName("Test save - throws ProductExistsException when product ID already exists")
    void testSaveThrowsProductExistsException() {
        Product existing = products.get(0); // iPhone 15 with id=1
        when(productRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThrows(ProductExistsException.class, () -> productService.save(existing),
                "Should throw ProductExistsException when product with same ID exists");
    }

    @Test
    @Order(3)
    @DisplayName("Test getById - returns correct product when ID exists")
    void testGetByIdFound() throws ProductNotFoundException {
        when(productRepository.findById(1)).thenReturn(Optional.of(products.get(0)));

        Product result = productService.getById(1);
        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
    }

    @Test
    @Order(4)
    @DisplayName("Test getById - throws ProductNotFoundException when ID does not exist")
    void testGetByIdNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getById(99),
                "Should throw ProductNotFoundException for non-existent id");
    }

    @Test
    @Order(5)
    @DisplayName("Test getAll - returns all products")
    void testGetAll() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAll();
        assertNotNull(result);
        assertEquals(5, result.size());
    }

    @Test
    @Order(6)
    @DisplayName("Test update - updates and returns the product successfully")
    void testUpdateSuccess() throws ProductNotFoundException {
        Product updated = Product.builder().id(1).name("iPhone 15 Pro")
                .maxRetailPrice(90000).isAvailable(true).build();

        when(productRepository.findById(1)).thenReturn(Optional.of(products.get(0)));
        when(productRepository.update(1, updated)).thenReturn(updated);

        Product result = productService.update(1, updated);
        assertEquals("iPhone 15 Pro", result.getName());
    }

    @Test
    @Order(7)
    @DisplayName("Test update - throws ProductNotFoundException when product does not exist")
    void testUpdateNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        Product dummy = Product.builder().id(99).name("Ghost").build();

        assertThrows(ProductNotFoundException.class, () -> productService.update(99, dummy));
    }

    @Test
    @Order(8)
    @DisplayName("Test delete - deletes product successfully")
    void testDeleteSuccess() {
        when(productRepository.findById(1)).thenReturn(Optional.of(products.get(0)));
        doNothing().when(productRepository).delete(1);  // void method mock

        // Mocking delete(int) → returns boolean — need to mock it properly
        when(productRepository.delete(1)).thenReturn(true);

        assertDoesNotThrow(() -> productService.delete(1));
        verify(productRepository, times(1)).delete(1);
    }

    @Test
    @Order(9)
    @DisplayName("Test delete - throws ProductNotFoundException when product does not exist")
    void testDeleteNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.delete(99));
    }

    // ===========================
    // FILTER TESTS
    // ===========================

    @Test
    @Order(10)
    @DisplayName("Test getProductsByAvailability - returns only available products")
    void testGetProductsByAvailability() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> available = productService.getProductsByAvailability(true);
        assertEquals(3, available.size(), "3 products should be available");
        assertTrue(available.stream().allMatch(Product::isAvailable));
    }

    @Test
    @Order(11)
    @DisplayName("Test getProductsByCategory - returns products in given category")
    void testGetProductsByCategory() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> laptops = productService.getProductsByCategory("Laptops");
        assertEquals(2, laptops.size(), "Should return 2 Laptop products");
        assertTrue(laptops.stream().allMatch(p -> p.getCategory().equals("Laptops")));
    }

    @Test
    @Order(12)
    @DisplayName("Test getProductsByPriceGreaterThan - returns products above given price")
    void testGetProductsByPriceGreaterThan() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getProductsByPriceGreaterThan(100000);
        assertEquals(2, result.size(), "MacBook (150000) and Dell (120000) are above 100000");
    }

    @Test
    @Order(13)
    @DisplayName("Test getProductsByPriceLessThan - returns products below given price")
    void testGetProductsByPriceLessThan() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getProductsByPriceLessThan(30000);
        assertEquals(2, result.size(), "Sony (25000) and Adidas (15000) are below 30000");
    }

    @Test
    @Order(14)
    @DisplayName("Test getProductsAfterYear - returns products manufactured after given year")
    void testGetProductsAfterYear() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getProductsAfterYear(2022);
        assertEquals(2, result.size(), "iPhone 15 and MacBook Pro manufactured in 2023");
    }

    @Test
    @Order(15)
    @DisplayName("Test getAvailableProductsAbovePrice - returns available products above price")
    void testGetAvailableProductsAbovePrice() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAvailableProductsAbovePrice(50000);
        assertEquals(2, result.size(), "iPhone 15 (available,80000) and MacBook Pro (available,150000)");
    }

    // ===========================
    // BASIC INFO TESTS
    // ===========================

    @Test
    @Order(16)
    @DisplayName("Test getAllProductNames - returns list of all product names")
    void testGetAllProductNames() {
        when(productRepository.findAll()).thenReturn(products);

        List<String> names = productService.getAllProductNames();
        assertEquals(5, names.size());
        assertTrue(names.contains("iPhone 15"));
        assertTrue(names.contains("MacBook Pro"));
    }

    @Test
    @Order(17)
    @DisplayName("Test countProductsBasedOnAvailability - counts correctly")
    void testCountProductsBasedOnAvailability() {
        when(productRepository.findAll()).thenReturn(products);

        long availableCount = productService.countProductsBasedOnAvailability(true);
        long unavailableCount = productService.countProductsBasedOnAvailability(false);

        assertEquals(3, availableCount, "3 products are available");
        assertEquals(2, unavailableCount, "2 products are not available");
    }

    @Test
    @Order(18)
    @DisplayName("Test hasProductFromCompany - returns true when company exists")
    void testHasProductFromCompanyFound() {
        when(productRepository.findAll()).thenReturn(products);
        assertTrue(productService.hasProductFromCompany("Apple"));
        assertTrue(productService.hasProductFromCompany("apple")); // case-insensitive
    }

    @Test
    @Order(19)
    @DisplayName("Test hasProductFromCompany - returns false when company does not exist")
    void testHasProductFromCompanyNotFound() {
        when(productRepository.findAll()).thenReturn(products);
        assertFalse(productService.hasProductFromCompany("Nike"));
    }

    @Test
    @Order(20)
    @DisplayName("Test areAllProductsAvailable - returns false when some products are unavailable")
    void testAreAllProductsAvailableFalse() {
        when(productRepository.findAll()).thenReturn(products);
        assertFalse(productService.areAllProductsAvailable(),
                "Not all products are available in sample data");
    }

    @Test
    @Order(21)
    @DisplayName("Test areAllProductsAvailable - returns true when all products are available")
    void testAreAllProductsAvailableTrue() {
        List<Product> allAvailable = Arrays.asList(
                Product.builder().id(1).isAvailable(true).build(),
                Product.builder().id(2).isAvailable(true).build()
        );
        when(productRepository.findAll()).thenReturn(allAvailable);
        assertTrue(productService.areAllProductsAvailable());
    }

    @Test
    @Order(22)
    @DisplayName("Test findFirstProduct - returns first product wrapped in Optional")
    void testFindFirstProduct() {
        when(productRepository.findAll()).thenReturn(products);

        Optional<Product> first = productService.findFirstProduct();
        assertTrue(first.isPresent());
        assertEquals("iPhone 15", first.get().getName());
    }

    @Test
    @Order(23)
    @DisplayName("Test getUniqueCategories - returns only distinct category names")
    void testGetUniqueCategories() {
        when(productRepository.findAll()).thenReturn(products);

        List<String> categories = productService.getUniqueCategories();
        // Electronics, Laptops, Audio, Footwear → 4 unique categories
        assertEquals(4, categories.size(), "Should return 4 unique categories");
        assertEquals(categories.stream().distinct().count(), categories.size(),
                "Categories should have no duplicates");
    }

    // ===========================
    // SORTING & TOP N TESTS
    // ===========================

    @Test
    @Order(24)
    @DisplayName("Test getTopNExpensiveProducts - returns top N products by price descending")
    void testGetTopNExpensiveProducts() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> top2 = productService.getTopNExpensiveProducts(2);
        assertEquals(2, top2.size());
        assertEquals("MacBook Pro", top2.get(0).getName(), "MacBook Pro is most expensive at 150000");
        assertEquals("Dell XPS 15", top2.get(1).getName(), "Dell XPS 15 is second at 120000");
    }

    @Test
    @Order(25)
    @DisplayName("Test sortProductsByPriceAsc - returns products sorted low to high")
    void testSortProductsByPriceAsc() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> sorted = productService.sortProductsByPriceAsc();
        assertEquals(5, sorted.size());
        assertEquals("Adidas Ultraboost", sorted.get(0).getName(), "Adidas (15000) should be first");
        assertEquals("MacBook Pro", sorted.get(4).getName(), "MacBook Pro (150000) should be last");

        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getMaxRetailPrice() <= sorted.get(i + 1).getMaxRetailPrice(),
                    "Products should be in ascending price order");
        }
    }

    @Test
    @Order(26)
    @DisplayName("Test sortProductsByNameDesc - returns products sorted Z to A by name")
    void testSortProductsByNameDesc() {
        when(productRepository.findAll()).thenReturn(products);

        List<Product> sorted = productService.sortProductsByNameDesc();
        assertEquals(5, sorted.size());

        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getName().compareTo(sorted.get(i + 1).getName()) >= 0,
                    "Products should be in descending name order");
        }
    }

    // ===========================
    // AGGREGATION TESTS
    // ===========================

    @Test
    @Order(27)
    @DisplayName("Test getTotalInventoryValue - returns sum of all product MRPs")
    void testGetTotalInventoryValue() {
        when(productRepository.findAll()).thenReturn(products);

        Integer total = productService.getTotalInventoryValue();
        // 80000 + 150000 + 25000 + 120000 + 15000 = 390000
        assertEquals(390000, total, "Total inventory value should be 390000");
    }

    @Test
    @Order(28)
    @DisplayName("Test getTotalDiscountedValue - returns total discount across all products")
    void testGetTotalDiscountedValue() {
        when(productRepository.findAll()).thenReturn(products);

        double totalDiscount = productService.getTotalDiscountedValue();
        // iPhone: 80000*10/100=8000, MacBook: 150000*5/100=7500,
        // Sony: 25000*20/100=5000, Dell: 120000*8/100=9600, Adidas: 15000*20/100=3000
        // Total = 33100
        assertEquals(33100.0, totalDiscount, 0.01, "Total discounted value should be 33100.0");
    }

    // ===========================
    // GROUPING & PARTITIONING TESTS
    // ===========================

    @Test
    @Order(29)
    @DisplayName("Test countProductsByCategory - returns count per category")
    void testCountProductsByCategory() {
        when(productRepository.findAll()).thenReturn(products);

        Map<String, Long> counts = productService.countProductsByCategory();
        assertNotNull(counts);
        assertEquals(1L, counts.get("Electronics"), "1 Electronics product");
        assertEquals(2L, counts.get("Laptops"), "2 Laptop products");
        assertEquals(1L, counts.get("Audio"), "1 Audio product");
        assertEquals(1L, counts.get("Footwear"), "1 Footwear product");
    }

    @Test
    @Order(30)
    @DisplayName("Test groupProductsByCategory - groups products by category correctly")
    void testGroupProductsByCategory() {
        when(productRepository.findAll()).thenReturn(products);

        Map<String, List<Product>> grouped = productService.groupProductsByCategory();
        assertNotNull(grouped);
        assertEquals(4, grouped.size(), "Should have 4 category groups");
        assertEquals(2, grouped.get("Laptops").size(), "Laptops group should have 2 products");
        assertEquals(1, grouped.get("Electronics").size());
    }

    @Test
    @Order(31)
    @DisplayName("Test groupProductsByCompany - groups products by company correctly")
    void testGroupProductsByCompany() {
        when(productRepository.findAll()).thenReturn(products);

        Map<String, List<Product>> grouped = productService.groupProductsByCompany();
        assertNotNull(grouped);
        assertEquals(2, grouped.get("Apple").size(), "Apple has 2 products");
        assertEquals(1, grouped.get("Sony").size());
    }

    @Test
    @Order(32)
    @DisplayName("Test partitionByAvailability - partitions into available and unavailable")
    void testPartitionByAvailability() {
        when(productRepository.findAll()).thenReturn(products);

        Map<Boolean, List<Product>> partitioned = productService.partitionByAvailability();
        assertNotNull(partitioned);
        assertEquals(3, partitioned.get(true).size(), "3 available products");
        assertEquals(2, partitioned.get(false).size(), "2 unavailable products");
    }

    // ===========================
    // MIN / MAX TESTS
    // ===========================

    @Test
    @Order(33)
    @DisplayName("Test getMaxPricedProduct - returns product with highest MRP")
    void testGetMaxPricedProduct() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(products);

        Product max = productService.getMaxPricedProduct();
        assertEquals("MacBook Pro", max.getName(), "MacBook Pro is most expensive at 150000");
        assertEquals(150000, max.getMaxRetailPrice());
    }

    @Test
    @Order(34)
    @DisplayName("Test getMaxPricedProduct - throws ProductNotFoundException when list is empty")
    void testGetMaxPricedProductEmpty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ProductNotFoundException.class, () -> productService.getMaxPricedProduct());
    }

    @Test
    @Order(35)
    @DisplayName("Test getMinPricedProduct - returns product with lowest MRP")
    void testGetMinPricedProduct() throws ProductNotFoundException {
        when(productRepository.findAll()).thenReturn(products);

        Product min = productService.getMinPricedProduct();
        assertEquals("Adidas Ultraboost", min.getName(), "Adidas (15000) is cheapest");
        assertEquals(15000, min.getMaxRetailPrice());
    }

    @Test
    @Order(36)
    @DisplayName("Test getMinPricedProduct - throws ProductNotFoundException when list is empty")
    void testGetMinPricedProductEmpty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(ProductNotFoundException.class, () -> productService.getMinPricedProduct());
    }

    // ===========================
    // MAP OPERATION TESTS
    // ===========================

    @Test
    @Order(37)
    @DisplayName("Test getProductMapById - returns map with product ID as key")
    void testGetProductMapById() {
        when(productRepository.findAll()).thenReturn(products);

        Map<Integer, Product> map = productService.getProductMapById();
        assertNotNull(map);
        assertEquals(5, map.size());
        assertEquals("iPhone 15", map.get(1).getName());
        assertEquals("MacBook Pro", map.get(2).getName());
    }

    @Test
    @Order(38)
    @DisplayName("Test getAveragePriceByCategory - returns correct average per category")
    void testGetAveragePriceByCategory() {
        when(productRepository.findAll()).thenReturn(products);

        Map<String, Double> avgMap = productService.getAveragePriceByCategory();
        assertNotNull(avgMap);

        // Laptops: (150000 + 120000) / 2 = 135000.0
        assertEquals(135000.0, avgMap.get("Laptops"), 0.01);
        // Electronics: 80000 / 1 = 80000.0
        assertEquals(80000.0, avgMap.get("Electronics"), 0.01);
        // Audio: 25000 / 1 = 25000.0
        assertEquals(25000.0, avgMap.get("Audio"), 0.01);
    }

    @Test
    @Order(39)
    @DisplayName("Test getTop3ProductsByCategory - returns max 3 products per category by price desc")
    void testGetTop3ProductsByCategory() {
        // Add extra Laptop products to test the limit(3)
        List<Product> extendedProducts = new ArrayList<>(products);
        extendedProducts.add(Product.builder().id(6).name("HP Spectre").maxRetailPrice(110000)
                .category("Laptops").company("HP").isAvailable(true).build());
        extendedProducts.add(Product.builder().id(7).name("Lenovo ThinkPad").maxRetailPrice(95000)
                .category("Laptops").company("Lenovo").isAvailable(true).build());

        when(productRepository.findAll()).thenReturn(extendedProducts);

        Map<String, List<Product>> top3 = productService.getTop3ProductsByCategory();
        assertNotNull(top3);

        List<Product> laptops = top3.get("Laptops");
        assertNotNull(laptops);
        assertTrue(laptops.size() <= 3, "Should return at most 3 laptops");
        // First should be MacBook Pro (150000)
        assertEquals("MacBook Pro", laptops.get(0).getName());
    }
}