package com.revolut.test.exception;

public class InvalidRequestMethodException extends Exception{

    public InvalidRequestMethodException() {
    }

    public InvalidRequestMethodException(String message) {
        super(message);
    }
}
