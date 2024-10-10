package com.example.demo.test.services;

import com.example.demo.models.Products.Product;
import com.example.demo.services.ProductService;

import com.example.demo.test.abstract_tests.AbstractTest;

import org.junit.Test;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Profile("test")
@Configuration
public class ProductServiceTest extends AbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Bean
    @Primary
    public ProductService productService() {
        return Mockito.mock(ProductService.class);
    }

    @Test
    public void getProducts() throws Exception {
        String uri = "/products";
        MvcResult mvcResult =
                mvc.perform(
                        MockMvcRequestBuilders.get(uri)
                                              .accept(
                                                      MediaType.APPLICATION_JSON_VALUE
                                              )
                ).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        Product[] products = super.mapFromJson(content, Product[].class);
        assertTrue(products.length > 0);
    }

    @Test
    public void createProduct() throws Exception {
        String uri = "/products";
        Product product = new Product();
        product.setId(1);
        product.setName("Ginger");
        String inputJson = super.mapToJson(product);
        MvcResult mvcResult =
                mvc.perform(
                        MockMvcRequestBuilders.post(uri)
                                              .contentType(
                                                      MediaType.APPLICATION_JSON
                                              )
                                              .content(inputJson)
                ).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is created successfully");
    }

    @Test
    public void updateProduct() throws Exception {
        String uri = "/prodeucts/2";
        Product product = new Product();
        product.setName("Lemon");
        String inputJson = super.mapToJson(product);
        MvcResult mvcResult =
                mvc.perform(
                        MockMvcRequestBuilders.put(uri)
                                              .contentType(
                                                      MediaType.APPLICATION_JSON
                                              )
                                              .content(inputJson)
                ).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is updated successfully");
    }

    @Test
    public void deleteProduct() throws Exception {
        String uri = "/products/2";
        MvcResult mvcResult =
                mvc.perform(MockMvcRequestBuilders.delete(uri))
                    .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is deleted successfully");
    }

}
