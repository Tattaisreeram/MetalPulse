package com.smarthmalik.metalpulse.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String SPOT_PRICE_CACHE   = "spotPrice";
    public static final String FULL_HISTORY_CACHE = "fullHistory";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer serializer = redisCacheSerializer();

        return RedisCacheManager.builder(factory)
                .withCacheConfiguration(SPOT_PRICE_CACHE,   ttlConfig(Duration.ofSeconds(60), serializer))
                .withCacheConfiguration(FULL_HISTORY_CACHE, ttlConfig(Duration.ofHours(1),    serializer))
                .build();
    }

    private static RedisCacheConfiguration ttlConfig(Duration ttl, GenericJackson2JsonRedisSerializer serializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    // PolymorphicTypeValidator restricts deserialization to our own types + JDK basics,
    // mitigating the known gadget-chain risk of DefaultTyping.EVERYTHING.
    private static GenericJackson2JsonRedisSerializer redisCacheSerializer() {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.smarthmalik.metalpulse")
                .allowIfSubType("java.util.ArrayList")
                .allowIfSubType("java.math.BigDecimal")
                .allowIfSubType("java.time.LocalDateTime")
                .build();

        ObjectMapper om = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
                .build();

        return new GenericJackson2JsonRedisSerializer(om);
    }
}
