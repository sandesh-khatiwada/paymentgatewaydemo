package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.TransactionRequestDTO;
import com.sandesh.paymentgatewaydemo.service.npi.NPIService;
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

    NPIService npiService;

    @PostMapping("/payment/process")
    public ResponseEntity<ApiResponse<Map<String,String>>> addTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO){
        return npiService.validateTransaction(transactionRequestDTO.getRefId());
    }


}
