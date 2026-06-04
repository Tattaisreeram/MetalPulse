package com.smarthmalik.metalpulse.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "token:blacklist:";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String token, long remainingMs) {
        if (remainingMs > 0) {
            redisTemplate.opsForValue().set(KEY_PREFIX + token, "1", remainingMs, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + token));
    }
}
