package com.example.demo.controllers;

import com.example.demo.models.Products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@RestController
public class ProductRestController {

    @Autowired
    RestTemplate rest;

    @RequestMapping(path="/template/products")
    public String getProducts() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(
                Arrays.asList(
                        MediaType.APPLICATION_JSON
                )
        );
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return rest.exchange(
                "http://localhost:8080" // TODO: env-var
                + "/products",
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();
    }

    @RequestMapping(
            path="/template/products",
            method=RequestMethod.POST
    )
    public String createProduct(@RequestBody Product product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(
                Arrays.asList(
                        MediaType.APPLICATION_JSON
                )
        );
        HttpEntity<Product> entity = new HttpEntity<>(product, headers);
        return rest.exchange(
                "http://localhost:8080" // TODO: env-var
                + "/products",
                HttpMethod.POST,
                entity,
                String.class
        ).getBody();
    }

    @RequestMapping(
            path="/template/products/{id}",
            method=RequestMethod.PUT
    )
    public String updateProduct(@PathVariable("id") String id, @RequestBody Product product) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(
                Arrays.asList(
                        MediaType.APPLICATION_JSON
                )
        );
        HttpEntity<Product> entity = new HttpEntity<>(product, headers);
        return rest.exchange(
                "http://localhost:8080" // TODO: env-var
                + "/products"
                + id,
                HttpMethod.PUT,
                entity,
                String.class
        ).getBody();
    }

    @RequestMapping(
            path="/template/products/{id}",
            method=RequestMethod.DELETE
    )
    public String deleteProduct(@PathVariable("id") String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(
                Arrays.asList(
                        MediaType.APPLICATION_JSON
                )
        );
        HttpEntity<Product> entity = new HttpEntity<>(headers);
        return rest.exchange(
                "http://localhost:8080" // TODO: env-var
                + "/products"
                + id,
                HttpMethod.DELETE,
                entity,
                String.class
        ).getBody();
    }

}
