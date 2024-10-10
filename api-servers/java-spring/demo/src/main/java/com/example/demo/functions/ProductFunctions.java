package com.example.demo.functions;

import com.example.demo.models.Products.Product;

import java.util.Collection;

public interface ProductFunctions {
    // * interface methods don't require explicit 'abstract' definition
    public Collection<Product> getProducts();
    public Product createProduct(Product product);
    public Product replaceProduct(Integer id, Product product);
    public Product updateProduct(Integer id, Product product);
    public Boolean deleteProduct(Integer id);

}
