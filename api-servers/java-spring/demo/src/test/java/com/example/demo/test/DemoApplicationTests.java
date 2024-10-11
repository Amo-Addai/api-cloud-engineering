package com.example.demo.test;

import com.example.demo.services.OrderService;
import com.example.demo.services.ProductService;

//import org.junit.Assert;
import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@ActiveProfiles("test")
//@RunWith(SpringJUnit4ClassRunner.class)
public class DemoApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Test
	void contextLoads() {

	}

	@Test
	public void whenUserIdIsProvided_thenRetrieveNameIsCorrect() {
		Mockito.when(orderService.getProductName())
				.thenReturn("Mock Product Name");
		String testName = orderService.getProductName();
//		Assert.assertEquals("Mock Product Name", testName);
	}

}
