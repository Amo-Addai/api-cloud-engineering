package com.example.demo.interceptors.config;

import com.example.demo.configurations.Configurations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Component
public class GeneralInterceptorConfig { // extends WebMvcConfigurerAdapter { // TODO: find WebMvcConfigurerAdapter.class

    //    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(Configurations.localeChangeInterceptor());
    }

}
