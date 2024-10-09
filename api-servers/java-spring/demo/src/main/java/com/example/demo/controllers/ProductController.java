package com.example.demo.controllers;

import com.example.demo.models.Products.Product;
import com.example.demo.services.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping(path="/products")
    public ResponseEntity<Collection<Product>> getProducts() {
        return new ResponseEntity<>(
                productService.getProducts(),
                HttpStatus.OK
        );
    }

    

}
