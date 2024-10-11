package com.example.demo.functions;

import com.example.demo.models.Products.Product;

import java.util.Collection;

public interface ProductFunctions {

    // * interface methods don't require explicit 'abstract' definition
    Collection<Product> getProducts();
    Product createProduct(Product product);
    Product replaceProduct(Integer id, Product product);
    Product updateProduct(Integer id, Product product);
    Boolean deleteProduct(Integer id);

}
