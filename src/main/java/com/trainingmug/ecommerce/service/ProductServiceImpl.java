package com.trainingmug.ecommerce.service;

import com.trainingmug.ecommerce.exception.ProductExistsException;
import com.trainingmug.ecommerce.exception.ProductNotFoundException;
import com.trainingmug.ecommerce.model.Product;
import com.trainingmug.ecommerce.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    //dependency
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product save(Product product) throws ProductExistsException {
        //check whether product already exists or not based on id

        productRepository.findById(product.getId())
                .ifPresent(p -> {
                    throw new ProductExistsException("Product already exists with id : " + product.getId());
                });
        return productRepository.save(product);
    }

    @Override
    public Product getById(int id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id : " + id + " not found!"));
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product update(int id, Product product) throws ProductNotFoundException {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not found with id: " + id));
        return productRepository.update(id, product);
    }

    @Override
    public void delete(int id) {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product Not found with id: " + id));
        productRepository.delete(id);
    }

    @Override
    public List<Product> getProductsByAvailability(boolean isAvailble) {
        return productRepository.findAll().stream()
                .filter(p -> p.isAvailable() == isAvailble)
                .toList();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findAll().stream()
                .filter(product -> product.getCategory().equals(category))
                .toList();
    }

    @Override
    public List<Product> getProductsByPriceGreaterThan(int price) {
        return productRepository.findAll().stream()
                .filter(product -> product.getMaxRetailPrice() > price)
                .toList();
    }

    @Override
    public List<Product> getProductsByPriceLessThan(int price) {
        return productRepository.findAll().stream()
                .filter(product -> product.getMaxRetailPrice() < price)
                .toList();
    }

    @Override
    public List<String> getAllProductNames() {
        return productRepository.findAll().stream()
                .map(Product::getName)
                .toList();
    }

    @Override
    public long countProductsBasedOnAvailability(boolean availableStatus) {
        return productRepository.findAll().stream().filter(product -> product.isAvailable() == availableStatus).count();
    }

    @Override
    public boolean hasProductFromCompany(String company) {
        return productRepository.findAll().stream()
                .anyMatch(p -> p.getCompany().equalsIgnoreCase(company));
    }

    // 7
    @Override
    public boolean areAllProductsAvailable() {
        return productRepository.findAll().stream()
                .allMatch(Product::isAvailable);
    }

    // 8
    @Override
    public Optional<Product> findFirstProduct() {
        return productRepository.findAll().stream().findFirst();
    }

    // 9
    @Override
    public List<String> getUniqueCategories() {
        return productRepository.findAll().stream()
                .map(Product::getCategory)
                .distinct()
                .toList();
    }

    // 10
    @Override
    public List<Product> getTopNExpensiveProducts(int n) {
        return productRepository.findAll().stream()
                .sorted((a, b) -> Double.compare(b.getMaxRetailPrice(), a.getMaxRetailPrice()))
                .limit(n)
                .toList();
    }

    // 11
    @Override
    public List<Product> sortProductsByPriceAsc() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getMaxRetailPrice))
                .toList();
    }

    // 12
    @Override
    public List<Product> sortProductsByNameDesc() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getName).reversed())
                .toList();
    }

    // 13
    @Override
    public Integer getTotalInventoryValue() {
        return productRepository.findAll().stream()
                .map(Product::getMaxRetailPrice)
                .reduce(0, Integer::sum);
    }

    // 14
    @Override
    public double getTotalDiscountedValue() {
        return productRepository.findAll().stream()
                .mapToDouble(p -> p.getMaxRetailPrice() * p.getDiscountPercentage() / 100)
                .reduce(0.0, Double::sum);
    }

    // 15
    @Override
    public List<Product> getProductsAfterYear(int year) {
        return productRepository.findAll().stream()
                .filter(p -> p.getManufacturedYear() > year)
                .toList();
    }

    // 16
    @Override
    public List<Product> getAvailableProductsAbovePrice(double price) {
        return productRepository.findAll().stream()
                .filter(Product::isAvailable)
                .filter(p -> p.getMaxRetailPrice() > price)
                .toList();
    }

    // 17
    @Override
    public Map<String, Long> countProductsByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
    }

    // 18
    @Override
    public Map<String, List<Product>> groupProductsByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(Product::getCategory));
    }

    // 19
    @Override
    public Map<String, List<Product>> groupProductsByCompany() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(Product::getCompany));
    }

    // 20
    @Override
    public Map<Boolean, List<Product>> partitionByAvailability() {
        return productRepository.findAll().stream()
                .collect(Collectors.partitioningBy(Product::isAvailable));
    }

    // 21
    @Override
    public Product getMaxPricedProduct() throws ProductNotFoundException {
        return productRepository.findAll().stream()
                .max(Comparator.comparing(Product::getMaxRetailPrice))
                .orElseThrow(() -> new ProductNotFoundException("No products available"));
    }

    // 22
    @Override
    public Product getMinPricedProduct() throws ProductNotFoundException {
        return productRepository.findAll().stream()
                .min(Comparator.comparing(Product::getMaxRetailPrice))
                .orElseThrow(() -> new ProductNotFoundException("No products available"));
    }

    // 23
    @Override
    public Map<Integer, Product> getProductMapById() {
        return productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
    }

    // 24
    @Override
    public Map<String, Double> getAveragePriceByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(Product::getMaxRetailPrice)
                ));
    }

    // 25
    @Override
    public Map<String, List<Product>> getTop3ProductsByCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted((a, b) -> Double.compare(b.getMaxRetailPrice(), a.getMaxRetailPrice()))
                                        .limit(3)
                                        .toList()
                        )
                ));
    }
}



