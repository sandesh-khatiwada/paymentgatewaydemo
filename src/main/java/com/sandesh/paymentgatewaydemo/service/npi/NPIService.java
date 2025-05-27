package com.sandesh.paymentgatewaydemo.service.npi;

import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface NPIService {
    ResponseEntity<ApiResponse<Map<String, String>>> validateTransaction(String refId);
}