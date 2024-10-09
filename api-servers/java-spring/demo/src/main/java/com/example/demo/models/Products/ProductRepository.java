package com.example.demo.models.Products;

import java.util.HashMap;

public class ProductRepository {

    private static HashMap<String, Product> productRepo = new HashMap<>();
    private HashMap<String, ProductRecord> productRecords = new HashMap<>();

    // test static singleton-prop to retrieve non-static productRecords
    private static ProductRepository _this = new ProductRepository();

    public static HashMap<String, Product> productRepo() {
        return productRepo;
    }

    public static HashMap<String, ProductRecord> productRecords() {
        return _this.productRecords;
    }

}
