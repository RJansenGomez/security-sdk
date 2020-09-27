package org.rjansen.sdk.security;

public interface CacheRepository {
    void save(String key, SecurityUser user);

    SecurityUser findById(String key, SecurityUser user);

    void remove(String key, SecurityUser user);
}
