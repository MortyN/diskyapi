package com.disky.api.Exceptions;

public class UserImageDeleteException extends Throwable{
    public UserImageDeleteException(String errorMessage) {
        super(errorMessage);
    }
    public UserImageDeleteException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
