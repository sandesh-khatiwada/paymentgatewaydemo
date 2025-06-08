package com.sandesh.paymentgatewaydemo.util;

import com.sandesh.paymentgatewaydemo.entity.OtpEntry;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class CacheInspectorUtil {

    private static final Logger logger = LoggerFactory.getLogger(CacheInspectorUtil.class);
    private final CacheManager cacheManager;

    public CacheInspectorUtil(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void inspectPendingPaymentsCache() {
        Cache cache = cacheManager.getCache("pendingPayments");
        if (cache == null) {
            logger.warn("Cache 'pendingPayments' not found");
            System.out.println("Cache 'pendingPayments' not found");
            return;
        }

        ConcurrentMap<Object, Object> nativeCache = (ConcurrentMap<Object, Object>) cache.getNativeCache();
        if (nativeCache.isEmpty()) {
            logger.info("PendingPayments cache is empty");
            System.out.println("PendingPayments cache is empty");
            return;
        }

        logger.info("PendingPayments Cache Contents:");
        System.out.println("PendingPayments Cache Contents:");
        nativeCache.forEach((key, value) -> {
            PaymentRequest pr = (PaymentRequest) value;
            String logMessage = String.format("Key: %s, Value: refId=%s, status=%s, userId=%s",
                    key, pr.getRefId(), pr.getStatus(), pr.getUserId());
            logger.info(logMessage);
            System.out.println(logMessage);
        });
    }

    public void inspectOtpsCache() {
        Cache cache = cacheManager.getCache("otps");
        if (cache == null) {
            logger.warn("Cache 'otps' not found");
            System.out.println("Cache 'otps' not found");
            return;
        }

        ConcurrentMap<Object, Object> nativeCache = (ConcurrentMap<Object, Object>) cache.getNativeCache();
        if (nativeCache.isEmpty()) {
            logger.info("Otps cache is empty");
            System.out.println("Otps cache is empty");
            return;
        }

        logger.info("Otps Cache Contents:");
        System.out.println("Otps Cache Contents:");
        nativeCache.forEach((key, value) -> {
            OtpEntry otpEntry = (OtpEntry) value;
            String logMessage = String.format("Key: %s, Value: otp=%s, createdAt=%s",
                    key, otpEntry.getOtp(), otpEntry.getCreatedAt());
            logger.info(logMessage);
            System.out.println(logMessage);
        });
    }
}