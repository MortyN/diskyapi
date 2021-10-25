package com.disky.api.Exceptions;

public class ScoreCardException extends Exception {
    public ScoreCardException(String errorMessage) {
        super(errorMessage);
    }
    public ScoreCardException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
