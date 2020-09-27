package org.rjansen.sdk.security.repository.redis;

import org.rjansen.sdk.security.CacheRepository;
import org.rjansen.sdk.security.SecurityUser;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisRepository implements CacheRepository {

    private HashOperations hashOperations;

    private RedisTemplate redisTemplate;
    private final long minutesSessionTtl;

    public RedisRepository(
            RedisTemplate redisTemplate,
            long minutesSessionTtl) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = this.redisTemplate.opsForHash();
        this.minutesSessionTtl = minutesSessionTtl;
    }

    @Override
    public void save(String key, SecurityUser user) {
        hashOperations.put(key, user.getUsername(), user);
        redisTemplate.expire(key, minutesSessionTtl, TimeUnit.MINUTES);
    }

    @Override
    public SecurityUser findById(String key, SecurityUser user) {
        return (SecurityUser) hashOperations.get(key, user.getUsername());
    }

    @Override
    public void remove(String key, SecurityUser user) {
        hashOperations.delete(key, user.getUsername());
    }
}
