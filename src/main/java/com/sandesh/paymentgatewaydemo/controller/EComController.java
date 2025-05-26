package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.service.ecom.EcomService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ecom")
@AllArgsConstructor
public class EComController {

    private final EcomService ecomService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<PaymentRequestDTO>> checkout(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO){
        return ecomService.checkout(paymentRequestDTO);
    }
}
