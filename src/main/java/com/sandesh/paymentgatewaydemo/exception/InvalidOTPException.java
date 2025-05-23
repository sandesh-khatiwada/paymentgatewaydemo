package com.sandesh.paymentgatewaydemo.exception;

public class InvalidOTPException extends RuntimeException {
    public InvalidOTPException(String message) {
        super(message);
    }
}
