package com.trainingmug.ecommerce.repository;

import com.trainingmug.ecommerce.model.Product;
import com.trainingmug.ecommerce.util.CsvParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Tests for ProductRepository.
 * Uses Mockito to mock CsvParser so tests don't depend on an actual CSV file.
 *
 * Pattern: create a mock CsvParser → stub getProductsFromCsv() → create ProductRepository.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductRepositoryTest {

    @Mock
    private CsvParser csvParser;

    private ProductRepository productRepository;

    private List<Product> sampleProducts;

    @BeforeEach
    void setUp() throws IOException {
        // Build a fresh list of sample products before each test
        sampleProducts = new ArrayList<>(Arrays.asList(
                Product.builder().id(1).name("iPhone 15").maxRetailPrice(80000)
                        .discountPercentage(10.0f).isAvailable(true)
                        .company("Apple").category("Electronics").manufacturedYear(2023).build(),
                Product.builder().id(2).name("MacBook Pro").maxRetailPrice(150000)
                        .discountPercentage(5.0f).isAvailable(true)
                        .company("Apple").category("Laptops").manufacturedYear(2023).build(),
                Product.builder().id(3).name("Sony WH-1000XM5").maxRetailPrice(25000)
                        .discountPercentage(20.0f).isAvailable(false)
                        .company("Sony").category("Audio").manufacturedYear(2022).build()
        ));

        // Mock the CsvParser to return our sample list
        when(csvParser.getProductsFromCsv()).thenReturn(sampleProducts);
        productRepository = new ProductRepository(csvParser);
    }

    @Test
    @Order(1)
    @DisplayName("Test ProductRepository loads products from CsvParser on construction")
    void testConstructorLoadsCsv() throws IOException {
        verify(csvParser, times(1)).getProductsFromCsv();
        List<Product> all = productRepository.findAll();
        assertNotNull(all, "findAll() should not return null");
        assertEquals(3, all.size(), "Should load 3 products from mocked CSV");
    }

    @Test
    @Order(2)
    @DisplayName("Test findAll returns all products")
    void testFindAll() {
        List<Product> result = productRepository.findAll();
        assertEquals(3, result.size());
        assertEquals("iPhone 15", result.get(0).getName());
    }

    @Test
    @Order(3)
    @DisplayName("Test findById returns correct product when ID exists")
    void testFindByIdFound() {
        Optional<Product> result = productRepository.findById(1);
        assertTrue(result.isPresent(), "Should find product with id=1");
        assertEquals("iPhone 15", result.get().getName());
    }

    @Test
    @Order(4)
    @DisplayName("Test findById returns empty Optional when ID does not exist")
    void testFindByIdNotFound() {
        Optional<Product> result = productRepository.findById(99);
        assertFalse(result.isPresent(), "Should return empty Optional for non-existent id");
    }

    @Test
    @Order(5)
    @DisplayName("Test save adds a new product and returns it")
    void testSave() {
        Product newProduct = Product.builder()
                .id(4).name("Dell XPS").maxRetailPrice(120000)
                .isAvailable(true).company("Dell").category("Laptops").build();

        Product saved = productRepository.save(newProduct);

        assertNotNull(saved);
        assertEquals(4, saved.getId());
        assertEquals(4, productRepository.findAll().size(), "List size should grow to 4 after save");
    }

    @Test
    @Order(6)
    @DisplayName("Test update replaces the existing product and returns updated product")
    void testUpdate() {
        Product updatedProduct = Product.builder()
                .id(1).name("iPhone 15 Pro").maxRetailPrice(90000)
                .isAvailable(true).company("Apple").category("Electronics").manufacturedYear(2023).build();

        Product result = productRepository.update(1, updatedProduct);

        assertEquals("iPhone 15 Pro", result.getName());
        assertEquals(90000, result.getMaxRetailPrice());

        // Verify in the list
        Optional<Product> fromList = productRepository.findById(1);
        assertTrue(fromList.isPresent());
        assertEquals("iPhone 15 Pro", fromList.get().getName());
    }

    @Test
    @Order(7)
    @DisplayName("Test delete by ID removes the product and returns true")
    void testDeleteById() {
        boolean deleted = productRepository.delete(1);
        assertTrue(deleted, "delete() should return true when product is found and removed");
        assertEquals(2, productRepository.findAll().size(), "List size should shrink to 2");
        assertFalse(productRepository.findById(1).isPresent(), "Deleted product should no longer be found");
    }

    @Test
    @Order(8)
    @DisplayName("Test delete by ID returns false when ID does not exist")
    void testDeleteByIdNotFound() {
        boolean deleted = productRepository.delete(99);
        assertFalse(deleted, "delete() should return false for non-existent id");
        assertEquals(3, productRepository.findAll().size(), "List size should remain 3");
    }

    @Test
    @Order(9)
    @DisplayName("Test delete by Product object removes the correct product")
    void testDeleteByObject() {
        Product toDelete = sampleProducts.get(0); // iPhone 15
        boolean deleted = productRepository.delete(toDelete);
        assertTrue(deleted, "delete(Product) should return true when product is found");
        assertEquals(2, productRepository.findAll().size());
    }
}