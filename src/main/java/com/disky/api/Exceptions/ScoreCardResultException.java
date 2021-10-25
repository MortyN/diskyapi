package com.disky.api.Exceptions;

public class ScoreCardResultException extends Exception {
    public ScoreCardResultException(String errorMessage) { super(errorMessage); }
    public ScoreCardResultException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
