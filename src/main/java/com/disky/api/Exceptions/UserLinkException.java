package com.disky.api.Exceptions;

public class UserLinkException extends Exception{
    public UserLinkException(String errorMessage) {
        super(errorMessage);
    }
    public UserLinkException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

}
