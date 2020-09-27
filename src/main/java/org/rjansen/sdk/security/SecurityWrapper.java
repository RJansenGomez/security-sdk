package org.rjansen.sdk.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityWrapper {

    private SecurityUser login;

    public String getCurrentUsername() {
        SecurityUser userDetails =
                (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

    public SecurityUser getCurrentUser() {
        SecurityUser userDetails = getUserFromSecurityContext();
        if (userDetails == null) {
            return login;
        }
        login = null;
        return userDetails;
    }

    private SecurityUser getUserFromSecurityContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null
                && context.getAuthentication() != null
                && context.getAuthentication().getPrincipal() != null
        ) {
            return (SecurityUser) context.getAuthentication().getPrincipal();
        }
        return null;

    }

    public void setLogin(SecurityUser userDetails) {
        this.login = userDetails;
    }
}
