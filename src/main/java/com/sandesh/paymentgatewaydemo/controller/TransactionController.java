package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.TransactionRequestDTO;
import com.sandesh.paymentgatewaydemo.service.TransactionService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TransactionController {

    TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse<Map<String,String>>> addTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO){
        return transactionService.addTransaction(transactionRequestDTO.getRefId());
    }
}
