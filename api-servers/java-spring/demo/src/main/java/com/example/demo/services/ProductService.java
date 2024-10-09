package com.example.demo.services;

import com.example.demo.functions.ProductFunctions;

import com.example.demo.models.Products.Product;
import com.example.demo.models.Products.ProductRecord;
import com.example.demo.models.Products.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Service
public class ProductService implements ProductFunctions {

    private static Map<String, Product> productRepo =
            ProductRepository.productRepo();

    private static Map<String, ProductRecord> productRecords =
            ProductRepository.productRecords();

    static {
        Product honey = new Product();
        honey.setId("1");
        honey.setName("Honey");
        productRepo.put(honey.getId(), honey);

        ProductRecord honeyRecord = new ProductRecord(
                honey.getId(),
                honey.getName(),
                Date.from(Instant.now())
        );
        productRecords.put(honeyRecord.id(), honeyRecord);

        Product almond = new Product();
        almond.setId("2");
        almond.setName("Almond");
        productRepo.put(almond.getId(), almond);

        ProductRecord almondRecord = new ProductRecord(
                almond.getId(),
                almond.getName(),
                Date.from(Instant.now())
        );
        productRecords.put(almondRecord.id(), almondRecord);
    }

    @Override
    public Collection<Product> getProducts() {
        return productRepo.values();
    }

    @Override
    public void createProduct(Product product) {
        ProductRecord record = new ProductRecord(
                product.getId(),
                product.getName(),
                Date.from(Instant.now())
        );
        productRepo.put(product.getId(), product);
        productRecords.put(record.id(), record);
    }

    @Override
    public void updateProduct(String id, Product product) {
        productRepo.remove(id); // remove by id (as key) in hashmap
        product.setId(id); // ensure new updated-product has same .id (if required)
        productRepo.put(id, product);

        // now, update product-records
        ProductRecord record = productRecords.get(id); // required for .createdAt

        /*
         * even-though records are immutable, forcing update

        record.id(id); // auto-gen'd getter .id() only (returns .id); no setter - immutable record

        * so recreate new record, to replace old one, in records hashMap instead
        * but keep old record's .createdAt
        */

        productRecords.remove(id);
        ProductRecord newRecord = new ProductRecord(
                product.getId(),
                product.getName(),
                record.createdAt() // .createdAt in product record
        );
        newRecord.updatedAt(Date.from(Instant.now()));
        productRecords.put(id, newRecord);
    }

    @Override
    public void deleteProduct(String id) {
        productRepo.remove(id);
    }

}
