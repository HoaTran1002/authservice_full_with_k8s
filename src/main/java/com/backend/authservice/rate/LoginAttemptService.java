package com.backend.authservice.rate;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final StringRedisTemplate redisTemplate;

    private final int MAX_ATTEMPTS = 5;
    private final Duration LOCK_DURATION = Duration.ofMinutes(15);

    public void loginSucceeded(String key) {
        redisTemplate.delete(counterKey(key));
        redisTemplate.delete(lockKey(key));
    }

    public void loginFailed(String key) {
        String k = counterKey(key);
        Long attempts = redisTemplate.opsForValue().increment(k);
        if (attempts == 1) redisTemplate.expire(k, Duration.ofMinutes(15));
        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(lockKey(key), "LOCKED", LOCK_DURATION);
        }
    }

    public boolean isLocked(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(key)));
    }

    private String counterKey(String key) { return "login:cnt:" + key; }
    private String lockKey(String key) { return "login:lock:" + key; }
}
