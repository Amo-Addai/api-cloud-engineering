package com.example.demo;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.logging.Logger;


@SpringBootApplication
@RestController
//@EnableConfigServer // app as Spring-Cloud Configuration Server
// RefreshScope for config client - fetches & loads the configuration properties value from the Config server
//@RefreshScope // app as Spring-Cloud Configuration Client (can't be both simultaneously)
//@EnableAuthorizationServer // Auth
//@EnableResourceServer
@EnableScheduling // Scheduling - cron-triggers, ..
//@EnableEurekaServer // app as Eureka Server
//@EnableEurekaClient // app as Eureka Client (can't be both simultaneously)
//@EnableZuulProxy // app as Zuul Proxy (Edge) Server
//@EnableAdminServer // app as Admin Server - to monitor & manage all servers (using their actuator endpoints)
//@EnableSwagger2 // for API documentation
public class DemoApplication {

	private static final java.util.logging.Logger logger =
			Logger.getLogger("java:logger");

	private static final org.slf4j.Logger logger4J =
			LoggerFactory.getLogger(DemoApplication.class);

	@Value("${welcome.message}") // config-server's (application-name-of-)config-client.properties file-data
	String welcomeText;


	// running on http://localhost:8080 (:9999 ?) | default content-type: application/json
	public static void main(String[] args) {
		logger.info("Starting Server"); // .warn/error/ fatal/off ?
		SpringApplication.run(DemoApplication.class, args);
		logger4J.info("Server running at port :8080");
	}

	// http://localhost:8080/
	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	@RequestMapping(path="/success") // * or value="/"
	public String success() {
		return "Success!";
	}

	@RequestMapping(value="/welcome")
	public String welcomeText() {
		return welcomeText;
	}

	// Google OAuth2
	@RequestMapping(value="/auth/google")
	public Principal user(Principal principal) {
		return principal;
	}

	@RequestMapping("/sample/template")
	public String renderTemplate() {
		return "sample-template";
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	/* TODO: Docket Bean to configure Swagger2
	@Bean
	public Docket sampleApi() {
		return new Docket(
				DocumentationType.SWAGGER_2
		)
		.select()
		.apis(
				RequestHandlerSelectors
						.basePackage(
								"com.example.demo"
						)
		)
		.build();
	}
	*/

}

// TODO: java: package org.springframework.web.service.annotation does not exist
