package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface TransactionService {
    ResponseEntity<ApiResponse<Map<String,String>>> addTransaction(String refId);
}
