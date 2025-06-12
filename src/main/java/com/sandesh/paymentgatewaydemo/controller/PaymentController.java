package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.dto.TransactionRequestDTO;
import com.sandesh.paymentgatewaydemo.service.payment.PaymentService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/payment-requests/status")
    public ResponseEntity<ApiResponse<PaymentRequestDTO>> validatePaymentRequest(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO){
            return paymentService.validatePaymentRequest(transactionRequestDTO.getRefId());
    }



}
