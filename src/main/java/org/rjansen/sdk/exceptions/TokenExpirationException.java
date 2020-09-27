package org.rjansen.sdk.exceptions;

public class TokenExpirationException extends RuntimeException {
    public TokenExpirationException(){
        super("Token expired");
    }
}
