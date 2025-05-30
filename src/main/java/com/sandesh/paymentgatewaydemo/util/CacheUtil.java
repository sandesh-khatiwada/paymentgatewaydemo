//package com.sandesh.paymentgatewaydemo.util;
//
//import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.cache.annotation.Cacheable;
//
//public class CacheUtil {
//
//    @CachePut(value = "pendingPayments", key = "#paymentRequest.refId")
//    public static PaymentRequest cachePaymentRequest(PaymentRequest paymentRequest) {
//        return paymentRequest;
//    }
//
//    @Cacheable(value = "pendingPayments", key = "#refId")
//    public static PaymentRequest getPendingPayment(String refId) {
//        return null; // Returns null if not found in cache
//    }
//}
