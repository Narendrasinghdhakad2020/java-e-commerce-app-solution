package com.trainingmug.ecommerce.factory;

import com.trainingmug.ecommerce.controller.ProductController;
import com.trainingmug.ecommerce.repository.ProductRepository;
import com.trainingmug.ecommerce.service.*;
import com.trainingmug.ecommerce.ui.ProductUI;
import com.trainingmug.ecommerce.util.CsvParser;

import java.io.IOException;

public class AppFactory {

    // =========================
    // CORE SINGLETON
    // =========================
    private static CsvParser csvParser;

    // =========================
    // PRODUCT FLOW
    // =========================
    private static ProductRepository productRepository;
    private static ProductService productService;
    private static ProductController productController;
    private static ProductUI productUI;

    // =========================
    // CSV READER
    // =========================
    public static CsvParser getCsvParser() {
        if (csvParser == null) {
            csvParser = new CsvParser();
        }
        return csvParser;
    }


    // =========================
    // PRODUCT DEPENDENCIES
    // =========================

    public static ProductRepository getProductRepository() throws IOException {
        if (productRepository == null) {
            productRepository = new ProductRepository(getCsvParser());
        }
        return productRepository;
    }

    public static ProductService getProductService() throws IOException {
        if (productService == null) {
            productService = new ProductServiceImpl(getProductRepository());
        }
        return productService;
    }

    public static ProductController getProductController() throws IOException {
        if (productController == null) {
            productController = new ProductController(getProductService());
        }
        return productController;
    }






}
