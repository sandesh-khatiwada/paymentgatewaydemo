package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface OtpService {
      ResponseEntity<ApiResponse<String>> validateAccess( String refId);
     ResponseEntity<ApiResponse<Void>> sendOtp(String refId);
     ResponseEntity<ApiResponse<Void>> verifyOtp(String otp ,String refId);

}
