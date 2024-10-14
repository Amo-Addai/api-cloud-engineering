package com.example.demo.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class MVCConfiguration {// implements WebMvcConfigurerAdapter {

//    @Override
    public void addViewController(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home"); // resources/templates
        registry.addViewController("/").setViewName("index"); // in resources/views/
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }

}
