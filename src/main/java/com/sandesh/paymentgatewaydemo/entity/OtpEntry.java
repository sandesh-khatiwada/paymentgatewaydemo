package com.sandesh.paymentgatewaydemo.entity;

import java.time.LocalDateTime;

public class OtpEntry {
    private final String otp;
    private final LocalDateTime createdAt;

    public OtpEntry(String otp) {
        this.otp = otp;
        this.createdAt = LocalDateTime.now();
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}