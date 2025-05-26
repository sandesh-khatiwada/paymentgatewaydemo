package com.sandesh.paymentgatewaydemo.service;

public interface PaymentRequestAccessValidator {
     boolean isPaymentRequestAccessValid(String email, String refId);
}
