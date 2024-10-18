package com.example.demo.websockets.controllers;

import org.springframework.stereotype.Controller;


/*
Message handling controller to work with STOMP messaging.
STOMP messages routed to @Controller
 */

@Controller
public class GreetingController {

    /*
    @MessageMapping("/hello")
    @SendTo("/topic/greetings") // subscription topic - clients (/resources/websocket.static/app.js) will subscribe to
    public Greeting greeting(HelloMessage msg)
        throws Exception {
        Thread.sleep(1000);
        return new Greeting(
                "Hello, "
                + message.getName()
                + "!"
        );
    }
    */

}
