package com.example.demo.interceptors.config;

import com.example.demo.interceptors.ProductControllerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Component
public class ProductInterceptorConfig { // extends WebMvcConfigurerAdapter { // TODO: find WebMvcConfigurerAdapter.class

    @Autowired
    ProductControllerInterceptor productControllerInterceptor;

//    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(productControllerInterceptor);
    }

}
