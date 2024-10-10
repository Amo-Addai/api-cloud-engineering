package com.example.demo.configurations.templates;

import org.springframework.context.annotation.Bean;

public class RedisTemplate {

    /*
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnFactory =
                new JedisConnectionFactory();
        jedisConnFactory.setHostName("localhost"); // todo: env-var
        jedisConnFactory.setPort(6000);
        jedisConnFactory.setUsePool(true);
        return jedisConnFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template =
                new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
    */

}
