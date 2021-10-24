package com.disky.api.Exceptions;

public class ArenaRoundException extends Exception{
    public ArenaRoundException(String errorMessage) {
        super(errorMessage);
    }
    public ArenaRoundException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
