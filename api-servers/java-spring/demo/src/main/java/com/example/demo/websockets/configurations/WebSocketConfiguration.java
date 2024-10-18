package com.example.demo.websockets.configurations;


import org.springframework.context.annotation.Configuration;


@Configuration
//@EnableWebSocketMessageBroker // configure Web socket message broker to create STOMP endpoints
public class WebSocketConfiguration
        {// extends AbstractWebSocketMessageBrokerConfigurer {

    /*

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry config
    ) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry
    ) {
        registry.addEndpoint(
                "/websocket-endpoint-url"
        ).withSockJS();
    }

    */

}
