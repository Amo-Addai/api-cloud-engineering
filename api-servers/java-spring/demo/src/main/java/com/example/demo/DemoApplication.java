package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
public class DemoApplication {

	// running on http://localhost:8080 (:9999 ?) | default content-type: application/json
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// http://localhost:8080/
	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
