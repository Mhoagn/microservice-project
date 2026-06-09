package com.myproject.auth_service.security;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "refresh:";

    public void save(String refreshToken, Long userId) {
        redisTemplate.opsForValue().set(
                PREFIX + refreshToken,
                String.valueOf(userId),
                Duration.ofDays(7)
        );
    }

    public boolean exists(String refreshToken) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + refreshToken)
        );
    }

    public void delete(String refreshToken) {
        redisTemplate.delete(PREFIX + refreshToken);
    }
}