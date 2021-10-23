package com.disky.api.Exceptions;

    public class ArenaException extends Exception{
        public ArenaException(String errorMessage) {
            super(errorMessage);
        }
        public ArenaException(String errorMessage, Throwable e) {
            super(errorMessage, e);
        }

    }

