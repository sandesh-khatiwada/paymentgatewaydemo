package com.sandesh.paymentgatewaydemo.service.payment;

import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidPaymentRequestException;
import com.sandesh.paymentgatewaydemo.mapper.PaymentRequestMapper;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.service.cache.PaymentCacheService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import com.sandesh.paymentgatewaydemo.util.CacheInspectorUtil;
import com.sandesh.paymentgatewaydemo.util.EmailExtractorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRequestMapper paymentRequestMapper;
    private final PaymentRequestAccessValidator paymentRequestAccessValidator;
    private final PaymentCacheService paymentCacheService;
    private final CacheInspectorUtil cacheInspectorUtil;


    @Override
    public ResponseEntity<ApiResponse<PaymentRequestDTO>> validatePaymentRequest(String refId) {

        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);

        if(paymentRequest==null){
            paymentRequest = paymentRequestRepository.findByRefId(refId).orElseThrow(()-> new IllegalArgumentException("Payment request not found for refId : "+refId));

        }

        String userEmail = EmailExtractorUtil.getEmailFromJwt();

        paymentRequestAccessValidator.isPaymentRequestAccessValid(userEmail,refId);



        if(paymentRequest.getAmount()<=0){
            paymentRequest.setStatus(Status.FAILED);
            paymentRequestRepository.save(paymentRequest);
            paymentCacheService.clearPaymentRequest(refId);
            throw new InvalidPaymentRequestException("Invalid Amount: "+paymentRequest.getAmount());
        }

        if(paymentRequest.getParticular().isEmpty()){
            paymentRequest.setStatus(Status.FAILED);
            paymentRequestRepository.save(paymentRequest);
            paymentCacheService.clearPaymentRequest(refId);
            throw new InvalidPaymentRequestException("Invalid Particular: "+paymentRequest.getParticular());
        }


        PaymentRequestDTO responseDTO = paymentRequestMapper.toDTO(paymentRequest);


        paymentCacheService.clearPaymentRequest(refId);
        cacheInspectorUtil.inspectPendingPaymentsCache();

        ApiResponse<PaymentRequestDTO> response = new ApiResponse<>(
                HttpStatus.OK,
                "Payment request validated successfully",
                responseDTO
        );

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
