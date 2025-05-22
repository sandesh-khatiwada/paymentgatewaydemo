package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.dto.LoginRequest;
import com.sandesh.paymentgatewaydemo.dto.LoginResponse;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
     ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest loginRequest);
}
