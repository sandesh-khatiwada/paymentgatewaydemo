package com.sandesh.paymentgatewaydemo.service.payment;

public interface PaymentRequestAccessValidator {
     boolean isPaymentRequestAccessValid(String email, String refId);
}
