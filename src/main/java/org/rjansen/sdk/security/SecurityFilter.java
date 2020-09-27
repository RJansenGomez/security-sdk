package org.rjansen.sdk.security;

import org.rjansen.sdk.exceptions.MessageWrapper;
import org.rjansen.sdk.exceptions.SessionExpiredException;
import org.rjansen.sdk.exceptions.TokenExpirationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class SecurityFilter extends OncePerRequestFilter {

    private final SecurityEncryptor encryptor;
    private final SecurityCache securityCache;

    public SecurityFilter(final SecurityEncryptor encryptor,
                          final CacheRepository cacheRepository) {
        this.encryptor = encryptor;
        this.securityCache = new SecurityCache(cacheRepository);
    }

    /**
     * Validates the token from the request
     *
     * @throws TokenExpirationException when the token is not active
     * @throws SessionExpiredException  when the token is not active
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String requestTokenHeader = request.getHeader("Authorization");
            SecurityUser user = encryptor.getUserFromJWT(requestTokenHeader);
            securityCache.checkSession(user);
            putUserInSecurityContext(user);
            securityCache.storeUser(user);
            filterChain.doFilter(request, response);
        } catch (SessionExpiredException | TokenExpirationException ex) {
            handleException(response, ex);
        }
    }

    private HttpServletResponse handleException(HttpServletResponse response, RuntimeException ex) throws IOException {
        MessageWrapper wrapper = MessageWrapper.wrapInfo(ex);
        ResponseEntity<MessageWrapper> responseEntity = new ResponseEntity<>(
                wrapper,
                HttpStatus.FORBIDDEN
        );
        response.setStatus(responseEntity.getStatusCodeValue());
        response.getWriter().write(responseEntity.getBody().toJson());
        return response;
    }

    private void putUserInSecurityContext(SecurityUser user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        null)
        );
    }
}
