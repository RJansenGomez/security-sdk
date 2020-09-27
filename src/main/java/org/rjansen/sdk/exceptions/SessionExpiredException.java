package org.rjansen.sdk.exceptions;

public class SessionExpiredException extends RuntimeException{
    public SessionExpiredException(){
        super("Session expired");
    }
}
