package com.revolut.test.exception;

public class InSufficientBalanceException extends Exception {

    public InSufficientBalanceException(String message) {
        super(message);
    }

    public InSufficientBalanceException() {
    }
}
