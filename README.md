# E-Commerce Product Capstone Project

## Overview

This is a pure Java (no Spring Boot) console-based capstone project that simulates the **product management module** of an e-commerce application. Data is loaded from a CSV file into an in-memory list, and all operations — CRUD, filtering, sorting, grouping, aggregation — are performed using the **Java Stream API**.

The project follows a strict **layered architecture**:

```
App → ProductUI → ProductController → ProductService → ProductRepository → CsvParser → products.csv
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/trainingmug/ecommerce/
│   │   ├── App.java                          ← Entry point
│   │   ├── controller/
│   │   │   └── ProductController.java        ← Delegates to service layer
│   │   ├── exception/
│   │   │   ├── ProductExistsException.java   ← Custom runtime exception
│   │   │   └── ProductNotFoundException.java ← Custom runtime exception
│   │   ├── factory/
│   │   │   └── AppFactory.java              ← Wires dependencies (manual DI)
│   │   ├── model/
│   │   │   └── Product.java                 ← Data model with Lombok
│   │   ├── repository/
│   │   │   └── ProductRepository.java       ← In-memory CRUD operations
│   │   ├── service/
│   │   │   ├── ProductService.java          ← Interface (contract)
│   │   │   └── ProductServiceImpl.java      ← Business logic with Streams
│   │   ├── ui/
│   │   │   ├── InputUtil.java               ← Console input helper (provided)
│   │   │   └── ProductUI.java               ← Console menu interface
│   │   └── util/
│   │       └── CsvParser.java               ← Reads products.csv into List<Product>
│   └── resources/
│       └── products.csv                     ← Product data file
└── test/
    └── java/com/trainingmug/ecommerce/
        ├── controller/ProductControllerTest.java
        ├── exception/ProductExceptionTest.java
        ├── model/ProductTest.java
        ├── repository/ProductRepositoryTest.java
        └── service/ProductServiceImplTest.java
```

---

## Objectives

1. Understand and implement **Layered Architecture** (UI → Controller → Service → Repository).
2. Apply the **Factory Pattern** for manual dependency injection.
3. Use **Lombok** annotations to reduce boilerplate code (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`).
4. Create and use **Custom Exceptions** (`ProductExistsException`, `ProductNotFoundException`).
5. Read and parse **CSV data** from the classpath using `BufferedReader`.
6. Implement all 25 **Stream API operations** including filter, map, sort, group, partition, reduce, and collect.


---

## Problem Statement 1: Model and Exceptions

### Task

1. **`Product.java`** — Add four Lombok annotations:
    - `@Data` → generates getters, setters, `toString`, `equals`, `hashCode`
    - `@NoArgsConstructor` → generates a no-argument constructor
    - `@AllArgsConstructor` → generates a constructor with all fields
    - `@Builder` → enables `Product.builder().name("X").build()` syntax

   Declare the following fields:
   ```java
   private int     id;
   private String  name;
   private int     maxRetailPrice;
   private float   discountPercentage;
   private int     rating;
   private boolean isAvailable;
   private String  company;
   private String  category;
   private int     manufacturedYear;
   ```

2. **`ProductExistsException.java`** — Make it extend `RuntimeException`. Add a constructor:
   ```java
   public ProductExistsException(String msg) { super(msg); }
   ```

3. **`ProductNotFoundException.java`** — Same pattern as above:
   ```java
   public ProductNotFoundException(String msg) { super(msg); }
   ```

---

## Problem Statement 2: CSV Parsing and Repository

### Task

4. **`CsvParser.java`** — Implement two methods:
    - `getProductsFromCsv()` — loads `products.csv` from the classpath using `getResourceAsStream`, reads it line by line using `BufferedReader`, and maps each line to a `Product` object.
    - `parseProduct(String line)` — splits a CSV row by comma and builds a `Product` using the builder pattern.

5. **`ProductRepository.java`** — Implement all CRUD methods:
    - `save(Product)` — adds to the in-memory list
    - `findById(int)` — streams the list and returns `Optional<Product>`
    - `findAll()` — returns the full list
    - `update(int, Product)` — uses `replaceAll()` to swap the matching product
    - `delete(int)` — uses `removeIf()` to remove by ID
    - `delete(Product)` — uses `remove()` to remove by object

---

## Problem Statement 3: Service Layer (Stream API)

### Task

6. **`ProductService.java`** — Declare all 25 method signatures in the interface (no bodies — just signatures). See the comments inside the file for each method's purpose.

7. **`ProductServiceImpl.java`** — Implement all 25 methods using the Java Stream API:

   | # | Method | Stream Operation |
      |---|--------|-----------------|
   | 1 | `save` | Check duplicate with `findById` + `ifPresent` |
   | 2 | `getById` | `findById` + `orElseThrow` |
   | 3 | `getAll` | Direct `findAll` |
   | 4 | `update` | Verify exists + delegate |
   | 5 | `delete` | Verify exists + delegate |
   | 6 | `getProductsByAvailability` | `filter` |
   | 7 | `getProductsByCategory` | `filter` |
   | 8 | `getProductsByPriceGreaterThan` | `filter` |
   | 9 | `getProductsByPriceLessThan` | `filter` |
   | 10 | `getProductsAfterYear` | `filter` |
   | 11 | `getAvailableProductsAbovePrice` | double `filter` |
   | 12 | `getAllProductNames` | `map` |
   | 13 | `countProductsBasedOnAvailability` | `filter` + `count` |
   | 14 | `hasProductFromCompany` | `anyMatch` |
   | 15 | `areAllProductsAvailable` | `allMatch` |
   | 16 | `findFirstProduct` | `findFirst` |
   | 17 | `getUniqueCategories` | `map` + `distinct` |
   | 18 | `getTopNExpensiveProducts` | `sorted` + `limit` |
   | 19 | `sortProductsByPriceAsc` | `sorted` with `Comparator` |
   | 20 | `sortProductsByNameDesc` | `sorted` reversed |
   | 21 | `getTotalInventoryValue` | `map` + `reduce` |
   | 22 | `getTotalDiscountedValue` | `mapToDouble` + `reduce` |
   | 23 | `countProductsByCategory` | `groupingBy` + `counting` |
   | 24 | `groupProductsByCategory` | `groupingBy` |
   | 25 | `groupProductsByCompany` | `groupingBy` |
   | 26 | `partitionByAvailability` | `partitioningBy` |
   | 27 | `getMaxPricedProduct` | `max` + `orElseThrow` |
   | 28 | `getMinPricedProduct` | `min` + `orElseThrow` |
   | 29 | `getProductMapById` | `toMap` |
   | 30 | `getAveragePriceByCategory` | `groupingBy` + `averagingDouble` |
   | 31 | `getTop3ProductsByCategory` | `groupingBy` + `collectingAndThen` + `limit` |

---

## Problem Statement 4: Controller, Factory, and UI

### Task

8. **`ProductController.java`** — Wire `ProductService` via constructor injection. Each method simply delegates to the corresponding `productService` method.

9. **`AppFactory.java`** — Implement lazy singleton creation for all four objects:
    - `getCsvParser()` → `new CsvParser()`
    - `getProductRepository()` → `new ProductRepository(getCsvParser())`
    - `getProductService()` → `new ProductServiceImpl(getProductRepository())`
    - `getProductController()` → `new ProductController(getProductService())`

10. **`ProductUI.java`** — Implement all five console operations (add, get, getAll, update, delete) using `InputUtil` to read user input and delegating to `productController`.

11. **`App.java`** — Create a `ProductUI` instance and call `ui.menu()`.

---

## CSV Format

The `products.csv` file in `src/main/resources/` must follow this format:

```csv
id,name,maxRetailPrice,discountPercentage,isAvailable,company,category,manufacturedYear
1,iPhone 15,80000,10.0,true,Apple,Electronics,2023
2,MacBook Pro,150000,5.0,true,Apple,Laptops,2023
```

---

## Running the Project

```bash
# Compile
mvn compile

# Run all tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.trainingmug.ecommerce.App"
```

---
