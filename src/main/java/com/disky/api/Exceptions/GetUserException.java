package com.disky.api.Exceptions;

public class GetUserException extends Exception{
    public GetUserException(String errorMessage) {
        super(errorMessage);
    }
    public GetUserException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

}
