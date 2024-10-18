package com.example.demo.controllers;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HystrixController {

    @RequestMapping(value="/")
    public String hello() throws InterruptedException {
        Thread.sleep(3000);
        return "Welcome Hystrix";
    }

    // now, add @Hystrix & @HystrixProperty to define timeout & fallback method
    @RequestMapping(value="/hello")
    @HystrixCommand(
            fallbackMethod = "fallback_hello",
            commandProperties = {
                    @HystrixProperty(
                            name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "1000"
                    )
            }
    )
    public String hello1() throws InterruptedException {
        Thread.sleep(3000);
        return "Welcome Hystrix";
    }

    // fallback method, if request takes a long time to respond
    private String fallback_hello() {
        return "Request fails. It takes long time to respond";
    }

}
