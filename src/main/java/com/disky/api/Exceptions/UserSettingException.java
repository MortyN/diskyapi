package com.disky.api.Exceptions;

public class UserSettingException extends Exception{
    public UserSettingException(String errorMessage) {
        super(errorMessage);
    }
    public UserSettingException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }

}
