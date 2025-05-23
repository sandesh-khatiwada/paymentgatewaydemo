package com.sandesh.paymentgatewaydemo.exception;

public class UnverifiedOtpException extends RuntimeException {
    public UnverifiedOtpException(String message) {
        super(message);
    }
}
