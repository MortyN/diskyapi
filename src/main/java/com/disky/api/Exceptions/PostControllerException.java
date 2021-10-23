package com.disky.api.Exceptions;

public class PostControllerException extends Exception{
    public PostControllerException(String errorMessage) {
        super(errorMessage);
    }
    public PostControllerException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

}
