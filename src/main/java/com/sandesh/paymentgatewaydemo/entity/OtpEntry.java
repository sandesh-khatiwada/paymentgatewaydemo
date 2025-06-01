package com.sandesh.paymentgatewaydemo.entity;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OtpEntry {
    private final String otp;
    private final LocalDateTime createdAt;

    public OtpEntry(String otp) {
        this.otp = otp;
        this.createdAt = LocalDateTime.now();
    }

}