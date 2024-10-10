package com.example.demo.models.Products;

import java.util.HashMap;

public class ProductRepository {

    private static HashMap<Integer, Product> productRepo = new HashMap<>();
    private HashMap<Integer, ProductRecord> productRecords = new HashMap<>();

    // test static singleton-prop to retrieve non-static productRecords
    private static ProductRepository _this = new ProductRepository();

    public static HashMap<Integer, Product> productRepo() {
        return productRepo;
    }

    public static HashMap<Integer, ProductRecord> productRecords() {
        return _this.productRecords;
    }

}
