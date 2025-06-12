
package com.sandesh.paymentgatewaydemo.service.ecom;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.mapper.PaymentRequestMapper;
import com.sandesh.paymentgatewaydemo.service.cache.PaymentCacheService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import com.sandesh.paymentgatewaydemo.util.CacheInspectorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@AllArgsConstructor
public class EcomServiceImpl implements EcomService {

    private final PaymentRequestMapper paymentRequestMapper;
    private final PaymentCacheService paymentCacheService;
    private final CacheInspectorUtil cacheInspectorUtil;



    @Override
    public ResponseEntity<ApiResponse<PaymentRequestDTO>> checkout(PaymentRequestDTO paymentRequestDTO) {
        try {
            PaymentRequest paymentRequest = paymentRequestMapper.toEntity(paymentRequestDTO);
            paymentRequest.setStatus(Status.PENDING);
            paymentRequest.setCreditStatus(Status.PENDING);
            paymentRequest.setDebitStatus(Status.PENDING);

            paymentRequest.setRefId(UUID.randomUUID().toString());

            paymentCacheService.cachePaymentRequest(paymentRequest);

            cacheInspectorUtil.inspectPendingPaymentsCache();

            PaymentRequestDTO paymentResponseDTO = paymentRequestMapper.toDTO(paymentRequest);

            String redirectURL = "/api/auth/login/"+paymentResponseDTO.getRefId();
            paymentResponseDTO.setRedirectURL(redirectURL);

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
