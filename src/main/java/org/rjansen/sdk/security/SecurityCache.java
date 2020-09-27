package org.rjansen.sdk.security;

import org.rjansen.sdk.exceptions.SessionExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityCache {
    private final CacheRepository cacheRepository;

    public SecurityCache(final CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public static String getUserSessionAttributeKey(SecurityUser user) {
        return "USER_ACTIVE_" + user.getUsername();
    }

    private void removeFromCache(SecurityUser currentUser) {
        cacheRepository.remove(getUserSessionAttributeKey(currentUser), currentUser);
    }

    public void cleanLoginData(SecurityUser currentUser) {
        SecurityContextHolder.getContext().setAuthentication(null);
        removeFromCache(currentUser);
    }

    public void storeUser(
            SecurityUser user
    ) {
        cacheRepository.save(getUserSessionAttributeKey(user), user);
    }

    public void checkSession(
            SecurityUser user
    ) {
        SecurityUser userSession = cacheRepository.findById(getUserSessionAttributeKey(user), user);
        if (userSession == null) {
            throw new SessionExpiredException();
        }
    }
}
