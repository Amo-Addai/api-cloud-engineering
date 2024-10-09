package com.example.demo.functions;

import com.example.demo.models.Products.Product;

import java.util.Collection;

public interface ProductFunctions {

    public abstract Collection<Product> getProducts();
    public abstract void createProduct(Product product);
    public abstract void updateProduct(String id, Product product);
    public abstract void deleteProduct(String id);

}
