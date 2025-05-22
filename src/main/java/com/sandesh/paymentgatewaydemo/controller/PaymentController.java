package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.service.PaymentService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @GetMapping("/payment-requests/{refId}")
    public ResponseEntity<ApiResponse<PaymentRequestDTO>> getPaymentRequest(@PathVariable("refId") String refId){
            return paymentService.getPaymentRequest(refId);
    }



}
