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

    private static Integer currentId = 0;

    private static Map<Integer, Product> productRepo =
            ProductRepository.productRepo();

    private static Map<Integer, ProductRecord> productRecords =
            ProductRepository.productRecords();

    static {
        currentId++;
        Product honey = new Product();
        honey.setId(currentId);
        honey.setName("Honey");
        productRepo.put(honey.getId(), honey);

        ProductRecord honeyRecord = new ProductRecord(
                honey.getId(),
                honey.getName(),
                Date.from(Instant.now())
        );
        productRecords.put(honeyRecord.id(), honeyRecord);

        currentId++;
        Product almond = new Product();
        almond.setId(currentId);
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
    public Product createProduct(Product product) {
        currentId++; product.setId(currentId);
        ProductRecord record = new ProductRecord(
                product.getId(),
                product.getName(),
                Date.from(Instant.now())
        );
        productRepo.put(product.getId(), product);
        productRecords.put(record.id(), record);
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println(productRecords);
        System.out.println("---------------------------------------------------------------------------------------------------------");
        return product;
    }

    @Override
    public Product replaceProduct(Integer id, Product product) {
        if (!productRepo.containsKey(id)) return null; // todo: fix response

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

        return product;
    }

    @Override
    public Product updateProduct(Integer id, Product product) {
        // TODO: update product in-place of productRepo & productRecords
        return product;
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        if (!productRepo.containsKey(id)) return null; // todo: fix response

        productRepo.remove(id);
        return !productRepo.containsKey(id);
    }

}
