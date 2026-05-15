package com.trainingmug.ecommerce.ui;

import com.trainingmug.ecommerce.controller.ProductController;
import com.trainingmug.ecommerce.factory.AppFactory;
import com.trainingmug.ecommerce.model.Product;

import java.util.List;

public class ProductUI {

    private ProductController productController;

    public ProductUI() {
        try {
            productController = AppFactory.getProductController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void menu() {
        System.out.println("\n1. Add New Product");
        System.out.println("2. Get Product Details");
        System.out.println("3. Get All Products");
        System.out.println("4. Update Product");
        System.out.println("5. Delete Product");

        int choice = InputUtil.getInt("Enter choice: ");

        switch (choice) {
            case 1 -> addProduct();
            case 2 -> getProduct();
            case 3 -> getAllProducts();
            case 4 -> updateProduct();
            case 5 -> deleteProduct();
        }
    }

    private void addProduct() {
        try {
            Product product = Product.builder().build();
            product.setName(InputUtil.getString("Name: "));
            product.setMaxRetailPrice(InputUtil.getInt("MRP: "));
            product.setDiscountPercentage(InputUtil.getFloat("Discount: "));
            product.setCompany(InputUtil.getString("Company: "));

            productController.save(product);

            System.out.println("Product added!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getProduct() {
        try {
            int id = InputUtil.getInt("Enter Product ID: ");
            printProduct(productController.getById(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllProducts() {
        List<Product> list = productController.getAll();
        printProductList(list);
    }

    private void updateProduct() {
        try {
            int id = InputUtil.getInt("Enter Product ID: ");
            Product product = productController.getById(id);

            product.setName(InputUtil.getString("New Name: "));
            productController.update(id, product);

            System.out.println("Updated!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteProduct() {
        try {
            int id = InputUtil.getInt("Enter Product ID: ");
            productController.delete(id);
            System.out.println("Deleted!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void printProductHeader() {

        System.out.printf(
                "%-5s %-20s %-10s %-10s %-10s %-12s %-15s %-15s %-8s%n",
                "ID", "Name", "MRP", "Discount%", "FinalPrice",
                "Available", "Company", "Category", "Year"
        );

        System.out.println("-----------------------------------------------------------------------------------------------");
    }


    private static String trim(String value, int max) {
        if (value == null) return "";
        return value.length() > max ? value.substring(0, max - 3) + "..." : value;
    }

    public  void printProductList(List<Product> products) {

        printProductHeader();

        for (Product p : products) {
            printProduct(p);
        }

        System.out.println("-----------------------------------------------------------------------------------------------\n");
    }

    public  void printProduct(Product p) {

        printProductHeader();

        int finalPrice = (int) (p.getMaxRetailPrice() -
                (p.getMaxRetailPrice() * p.getDiscountPercentage() / 100));

        System.out.printf(
                "%-5d %-20s %-10d %-10.2f %-10d %-12s %-15s %-15s %-8d%n",
                p.getId(),
                trim(p.getName(), 20),
                p.getMaxRetailPrice(),
                p.getDiscountPercentage(),
                finalPrice,
                p.isAvailable() ? "YES" : "NO",
                trim(p.getCompany(), 15),
                trim(p.getCategory(), 15),
                p.getManufacturedYear()
        );
    }
}

