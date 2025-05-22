package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OtpService {

     ResponseEntity<ApiResponse<Void>> sendOtp();
     ResponseEntity<ApiResponse<Void>> verifyOtp(String otp);



}
