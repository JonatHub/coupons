package com.meli.coupons.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, Float> reactiveRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
        RedisSerializationContext<String, Float> serializationContext = RedisSerializationContext
                .<String, Float>newSerializationContext(new StringRedisSerializer())
                .value(new FloatRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
    }
}



