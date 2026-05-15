package com.trainingmug.ecommerce.util;

import com.trainingmug.ecommerce.model.Product;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class CsvParser {

    public List<Product> getProductsFromCsv() throws IOException {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("products.csv");

        if (is == null) {
            throw new RuntimeException("❌ products.csv not found in resources");
        }
        //try-with-resources
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return br.lines()
                    .skip(1) // skip header
                    .map(this::parseProduct)
                    .toList();
        }
    }

    private Product parseProduct(String line) {

        String[] split = line.split(",");

        if (split.length < 8) {
            throw new RuntimeException("Invalid CSV row: " + line);
        }

        try {
            return Product.builder()
                    .id(Integer.parseInt(split[0].trim()))
                    .name(split[1].trim())
                    .maxRetailPrice(Integer.parseInt(split[2].trim()))
                    .discountPercentage(Float.parseFloat(split[3].trim()))
                    .isAvailable(Boolean.parseBoolean(split[4].trim()))
                    .company(split[5].trim())
                    .category(split[6].trim())
                    .manufacturedYear(Integer.parseInt(split[7].trim()))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing row: " + line, e);
        }
    }

    // =========================
    // DATE PARSER (SAFE)
    // =========================

    private LocalDateTime parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDateTime.parse(value.trim());
    }
}
