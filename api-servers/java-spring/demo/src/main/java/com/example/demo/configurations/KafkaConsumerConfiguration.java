package com.example.demo.configurations;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
//@EnableKafka // Spring - Apache Kafka service
public class KafkaConsumerConfiguration {

    /*

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:2181"
        );
        configProps.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "group-id"
        );
        configProps.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        configProps.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        // current Interface - DefaultKafkaConsumerFactoryCustomizer
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean // current Type - ConcurrentKafkaListenerContainerFactoryConfigurer
    public ConcurrentKafkaListenerContainerFactory<String, String>
            kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String>
        factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    */

}
