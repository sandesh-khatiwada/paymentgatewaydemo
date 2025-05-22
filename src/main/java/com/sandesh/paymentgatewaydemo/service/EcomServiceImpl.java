
package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.mapper.PaymentRequestMapper;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class EcomServiceImpl implements EcomService {

    private PaymentRequestRepository paymentRequestRepository;
    private PaymentRequestMapper paymentRequestMapper;

    @Override
    public ResponseEntity<ApiResponse<PaymentRequestDTO>> checkout(PaymentRequestDTO paymentRequestDTO) {
        try {
            PaymentRequest paymentRequest = paymentRequestMapper.toEntity(paymentRequestDTO);
            paymentRequest.setStatus(Status.PENDING);
            paymentRequest.setRefId(UUID.randomUUID().toString());

            PaymentRequest savedPaymentRequest = paymentRequestRepository.save(paymentRequest);

            PaymentRequestDTO paymentResponseDTO = paymentRequestMapper.toDTO(savedPaymentRequest);

            ApiResponse<PaymentRequestDTO> response = new ApiResponse<>(
                    HttpStatus.OK,
                    "Payment request made successfully",
                    paymentResponseDTO
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid application value: " + e.getMessage());
        }
    }
}
