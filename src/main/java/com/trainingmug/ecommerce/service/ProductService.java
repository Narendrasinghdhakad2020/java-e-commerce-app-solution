package com.trainingmug.ecommerce.service;

import com.trainingmug.ecommerce.exception.ProductExistsException;
import com.trainingmug.ecommerce.exception.ProductNotFoundException;
import com.trainingmug.ecommerce.model.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    /*
    CRUD
     */
    Product save(Product product) throws ProductExistsException;
    Product getById(int id) throws ProductNotFoundException;
    List<Product> getAll();
    Product update(int id, Product product) throws ProductNotFoundException;
    void delete(int id) throws ProductNotFoundException;


    //Get all available products based on availability
    List<Product> getProductsByAvailability(boolean isAvaialble);

    //Get all products belonging to a given category.
    List<Product> getProductsByCategory(String category);

    //Get all products with price greater than a given value.
    List<Product> getProductsByPriceGreaterThan(int price);

    //Get all products with price lesser than a given value.
    List<Product> getProductsByPriceLessThan(int price);

    //Get names of all products.
    List<String> getAllProductNames();

    //Count how many products are available.
    long countProductsBasedOnAvailability(boolean availableStatus);

    boolean hasProductFromCompany(String company);

    boolean areAllProductsAvailable();

    Optional<Product> findFirstProduct();

    List<String> getUniqueCategories();

    List<Product> getTopNExpensiveProducts(int n);

    List<Product> sortProductsByPriceAsc();

    List<Product> sortProductsByNameDesc();

    Integer getTotalInventoryValue();

    double getTotalDiscountedValue();

    List<Product> getProductsAfterYear(int year);

    List<Product> getAvailableProductsAbovePrice(double price);

    Map<String, Long> countProductsByCategory();

    Map<String, List<Product>> groupProductsByCategory();

    Map<String, List<Product>> groupProductsByCompany();

    Map<Boolean, List<Product>> partitionByAvailability();

    Product getMaxPricedProduct() throws ProductNotFoundException;

    Product getMinPricedProduct() throws ProductNotFoundException;

    Map<Integer, Product> getProductMapById();

    Map<String, Double> getAveragePriceByCategory();

    Map<String, List<Product>> getTop3ProductsByCategory();

}
