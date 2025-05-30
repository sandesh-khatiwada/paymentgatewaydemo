package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.entity.OtpEntry;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;

import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class PaymentCacheService {

    private final CacheManager cacheManager;
    private final PaymentRequestRepository paymentRequestRepository;

    public PaymentCacheService(CacheManager cacheManager, PaymentRequestRepository paymentRequestRepository) {
        this.cacheManager = cacheManager;
        this.paymentRequestRepository = paymentRequestRepository;
    }

    public PaymentRequest cachePaymentRequest(PaymentRequest paymentRequest) {
        Cache cache = cacheManager.getCache("pendingPayments");
        if (cache == null) {
            throw new IllegalStateException("Cache 'pendingPayments' not found");
        }

            cache.put(paymentRequest.getRefId(), paymentRequest);

        return paymentRequest;
    }

    public PaymentRequest getPendingPayment(String refId) {
        Cache cache = cacheManager.getCache("pendingPayments");
        if (cache == null) {
            throw new IllegalStateException("Cache 'pendingPayments' not found");
        }
        Cache.ValueWrapper wrapper = cache.get(refId);
        return wrapper != null ? (PaymentRequest) wrapper.get() : null;
    }

    public void cacheOtp(String refId, String otp) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            throw new IllegalStateException("Cache 'otps' not found");
        }
        if (otp != null) {
            cache.put(refId, new OtpEntry(otp));
        }
    }

    public OtpEntry getOtpEntry(String refId) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            throw new IllegalStateException("Cache 'otps' not found");
        }
        Cache.ValueWrapper wrapper = cache.get(refId);
        return wrapper != null ? (OtpEntry) wrapper.get() : null;
    }

    public void clearOtp(String refId) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            return;
        }
        cache.evict(refId);
    }

    public void clearOtpByUserEmail(String email) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            return;
        }
        Cache paymentCache = cacheManager.getCache("pendingPayments");
        if (paymentCache == null) {
            return;
        }

        ConcurrentMap<Object, Object> nativeCache = (ConcurrentMap<Object, Object>) cache.getNativeCache();
        ConcurrentMap<Object, Object> paymentNativeCache = (ConcurrentMap<Object, Object>) paymentCache.getNativeCache();

        paymentNativeCache.forEach((key, value) -> {
            PaymentRequest pr = (PaymentRequest) value;
            if (pr.getUser() != null && email.equals(pr.getUser().getEmail())) {
                nativeCache.remove(key);
            }
        });
    }
}