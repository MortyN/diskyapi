package com.disky.api.Exceptions;

public class UserSettingsMatrixException extends Exception {

        public UserSettingsMatrixException(String errorMessage) {
            super(errorMessage);
        }
        public UserSettingsMatrixException(String errorMessage, Throwable e) {
            super(errorMessage, e);
        }
}
