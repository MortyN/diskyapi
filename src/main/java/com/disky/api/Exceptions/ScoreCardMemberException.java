package com.disky.api.Exceptions;

public class ScoreCardMemberException extends Exception {
    public ScoreCardMemberException(String errorMessage) {
        super(errorMessage);
    }
    public ScoreCardMemberException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
