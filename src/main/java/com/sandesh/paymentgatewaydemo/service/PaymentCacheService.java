package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.entity.OtpEntry;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;

public interface PaymentCacheService {

     PaymentRequest cachePaymentRequest(PaymentRequest paymentRequest);
     PaymentRequest getPendingPayment(String refId);
     void cacheOtp(String refId, String otp);
     OtpEntry getOtpEntry(String refId);
     void clearOtp(String refId);
     void clearOtpByUserEmail(String email);
     void clearPaymentRequest(String refId);
}
