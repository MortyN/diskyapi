package com.disky.api.Exceptions;

public class UserImageUploadException extends Throwable {
    public UserImageUploadException(String errorMessage) {
        super(errorMessage);
    }
    public UserImageUploadException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
