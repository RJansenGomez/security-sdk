package org.rjansen.sdk.security.test;

import org.rjansen.sdk.security.SecurityCache;
import org.rjansen.sdk.security.SecurityEncryptor;
import org.rjansen.sdk.security.SecurityFilter;
import org.rjansen.sdk.security.SecurityUser;
import org.rjansen.sdk.security.repository.redis.RedisRepository;
import org.springframework.stereotype.Component;

@Component
public class LoginMock {

    private final SecurityCache securityCache;
    private final SecurityEncryptor encryptor;

    public LoginMock(final SecurityEncryptor encryptor, final RedisRepository redisRepository) {
        this.securityCache = new SecurityCache(redisRepository);
        this.encryptor = encryptor;
    }

    public String mockLogin(String user) {
        SecurityUser securityUser = new SecurityUser(user);
        String jwt = encryptor.createJWT(securityUser, 30);
        securityCache.storeUser(securityUser);
        return jwt;
    }

    public String mockLoginExpiredJWT(String user) {
        SecurityUser securityUser = new SecurityUser(user);
        String jwt = encryptor.createJWT(securityUser, -1);
        securityCache.storeUser(securityUser);
        return jwt;
    }

    public String mockLoginSessionExpired(String user) {
        SecurityUser securityUser = new SecurityUser(user);
        return encryptor.createJWT(securityUser, 20);
    }
}
