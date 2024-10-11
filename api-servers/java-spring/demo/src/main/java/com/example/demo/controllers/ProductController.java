package com.example.demo.controllers;

import com.example.demo.models.Products.Product;
import com.example.demo.services.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class ProductController {

    @Autowired // * dependency-injected ?
    ProductService productService;

    @RequestMapping(path="/products")
    public ResponseEntity<Collection<Product>> getProducts() {
        return new ResponseEntity<>(
                productService.getProducts(),
                HttpStatus.OK // or HttpStatusCode.valueOf(201)
        );
    }

    @RequestMapping(path="/products", method=RequestMethod.POST)
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // productService.createProduct(product);
        // * if productService returned void, success/error response-string returned to ResponseEntity instead
        return new ResponseEntity<>(
                productService.createProduct(product), // returns Product
                HttpStatus.CREATED
        );
    }

    @RequestMapping(path="/products/{id}", method=RequestMethod.PUT)
    public ResponseEntity<Product> replaceProduct(
            @PathVariable("id") String id,
            @RequestBody Product product
    ) {
        return new ResponseEntity<>(
                productService.replaceProduct(Integer.parseInt(id), product),
                HttpStatus.OK
        );
    }

    @RequestMapping(path="/products/{id}", method=RequestMethod.PATCH)
    public ResponseEntity<Product> updateProduct(
            @PathVariable("id") String id,
            @RequestBody Product product
    ) {
        return new ResponseEntity<>(
                productService.updateProduct(Integer.parseInt(id), product),
                HttpStatus.OK
        );
    }

    @RequestMapping(path="/products/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteProduct(@PathVariable("id") String id) {
        return new ResponseEntity<>(
                productService.deleteProduct(Integer.parseInt(id)),
                HttpStatus.OK
        );
    }

}
