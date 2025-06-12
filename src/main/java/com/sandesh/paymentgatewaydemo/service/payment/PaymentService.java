package com.sandesh.paymentgatewaydemo.service.payment;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    ResponseEntity<ApiResponse<PaymentRequestDTO>> validatePaymentRequest(String refId);

}
