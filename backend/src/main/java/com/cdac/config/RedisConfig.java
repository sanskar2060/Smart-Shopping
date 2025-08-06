package com.cdac.config;

import com.cdac.entity.ProductListWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

<<<<<<< HEAD
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
=======
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
>>>>>>> b5bc295d65dcecd9dbb704f1a88fdd6733045368
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
<<<<<<< HEAD
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        // Password not required or used
=======
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
>>>>>>> b5bc295d65dcecd9dbb704f1a88fdd6733045368
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, ProductListWrapper> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, ProductListWrapper> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<ProductListWrapper> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(ProductListWrapper.class);
        jsonSerializer.setObjectMapper(new ObjectMapper());

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}