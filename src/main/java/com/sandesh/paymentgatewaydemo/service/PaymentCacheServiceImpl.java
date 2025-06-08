package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.entity.OtpEntry;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;

import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Service
@AllArgsConstructor
public class PaymentCacheServiceImpl implements PaymentCacheService {

    private final CacheManager cacheManager;
    private final UserRepository userRepository;


    @Override
    public PaymentRequest cachePaymentRequest(PaymentRequest paymentRequest) {
        Cache cache = cacheManager.getCache("pendingPayments");
        if (cache == null) {
            throw new IllegalStateException("Cache 'pendingPayments' not found");
        }

        cache.put(paymentRequest.getRefId(), paymentRequest);

        return paymentRequest;
    }

    @Override
    public PaymentRequest getPendingPayment(String refId) {
        Cache cache = cacheManager.getCache("pendingPayments");
        if (cache == null) {
            throw new IllegalStateException("Cache 'pendingPayments' not found");
        }
        Cache.ValueWrapper wrapper = cache.get(refId);
        return wrapper != null ? (PaymentRequest) wrapper.get() : null;
    }

    @Override
    public void cacheOtp(String refId, String otp) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            throw new IllegalStateException("Cache 'otps' not found");
        }
        if (otp != null) {
            cache.put(refId, new OtpEntry(otp));
        }
    }

    @Override
    public OtpEntry getOtpEntry(String refId) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            throw new IllegalStateException("Cache 'otps' not found");
        }
        Cache.ValueWrapper wrapper = cache.get(refId);
        return wrapper != null ? (OtpEntry) wrapper.get() : null;
    }

    @Override
    public void clearOtp(String refId) {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            return;
        }
        cache.evict(refId);
    }

    @Override
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
            if (pr.getUserId() != null) {
                Optional<User> userOpt = userRepository.findById(pr.getUserId());
                if (userOpt.isPresent() && email.equals(userOpt.get().getEmail())) {
                    nativeCache.remove(key);
                }
            }
        });
    }

    @Override
    public void clearPaymentRequest(String refId) {
        Cache cache = cacheManager.getCache("pendingPayments");
        if (cache == null) {
            return;
        }
        cache.evict(refId);
    }
}