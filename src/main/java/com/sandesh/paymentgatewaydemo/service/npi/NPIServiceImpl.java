package com.sandesh.paymentgatewaydemo.service.npi;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidTransactionRequestException;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.service.PaymentCacheService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import com.sandesh.paymentgatewaydemo.util.CacheInspectorUtil;
import com.sandesh.paymentgatewaydemo.util.EmailExtractorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;


@Service
@AllArgsConstructor

public class NPIServiceImpl implements NPIService {

     private final UserRepository userRepository;
     private final PaymentRequestRepository paymentRequestRepository;
     private final PaymentCacheService paymentCacheService;
     private final CacheInspectorUtil cacheInspectorUtil;

     @Override
     @Transactional(noRollbackFor = InvalidTransactionRequestException.class)
     public ResponseEntity<ApiResponse<Map<String, String>>> validateTransaction(String refId){
          String email = EmailExtractorUtil.getEmailFromJwt();


          //user and paymentRequest exists for sure (verified by above method call)
          User user =userRepository.findByEmail(email).get();
          PaymentRequest paymentRequest = paymentRequestRepository.findByRefId(refId).get();

          User merchant = userRepository.findByEmail("merchant@gmail.com").get();


          if(paymentRequest.getStatus().equals(Status.SUCCESS)){
               paymentCacheService.clearPaymentRequest(refId);
               throw new InvalidTransactionRequestException("Transaction with the provided refId has already been completed");

          }

          if(!paymentRequest.getStatus().equals(Status.AUTHENTICATED)){
               throw new InvalidTransactionRequestException("Authentication Required");
          }

          if (paymentRequest.getAmount() <= 0) {

               paymentRequest.setDebitStatus(Status.FAILED);
               paymentRequest.setCreditStatus(Status.FAILED);
               paymentRequest.setStatus(Status.FAILED);
               paymentRequestRepository.save(paymentRequest);
               paymentCacheService.clearPaymentRequest(refId);
               throw new InvalidTransactionRequestException("Invalid Amount: Rs." + paymentRequest.getAmount());
          }


          //debit process

          double newUserBalance = user.getBalance() - paymentRequest.getAmount();
          if (newUserBalance < 0) {

               paymentRequest.setDebitStatus(Status.FAILED);
               paymentRequest.setCreditStatus(Status.FAILED);
               paymentRequest.setStatus(Status.FAILED);
               paymentRequestRepository.save(paymentRequest);
               paymentCacheService.clearPaymentRequest(refId);
               throw new InvalidTransactionRequestException("Insufficient balance: Rs." + user.getBalance());
          }


          user.setBalance(newUserBalance);
          userRepository.save(user);

          paymentRequest.setDebitStatus(Status.SUCCESS);


          double newMerchantBalance = merchant.getBalance() + paymentRequest.getAmount();

          // Credit process
          merchant.setBalance(newMerchantBalance);
          userRepository.save(merchant);
          paymentRequest.setCreditStatus(Status.SUCCESS);

          //set payment status to success
          paymentRequest.setStatus(Status.SUCCESS);

          paymentRequestRepository.save(paymentRequest);
          paymentCacheService.clearPaymentRequest(refId);

          cacheInspectorUtil.inspectPendingPaymentsCache();


          Map<String, String> responseData = new HashMap<>();


          responseData.put("transactionId", paymentRequest.getRefId());
          responseData.put("Debit Status", paymentRequest.getDebitStatus().toString());
          responseData.put("Credit Status", paymentRequest.getCreditStatus().toString());
          responseData.put("Status",paymentRequest.getStatus().toString());

          ApiResponse<Map<String, String>> response = new ApiResponse<>(
                  HttpStatus.OK,
                  "Transaction processed successfully",
                  "/success",
                  "/failure"
          );

          return new ResponseEntity<>(response, HttpStatus.OK);


     }

}
