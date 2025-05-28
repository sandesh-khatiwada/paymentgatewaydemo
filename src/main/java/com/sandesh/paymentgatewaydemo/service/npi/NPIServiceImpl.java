package com.sandesh.paymentgatewaydemo.service.npi;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidTransactionRequestException;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.service.PaymentRequestAccessValidator;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
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

     private UserRepository userRepository;
     private PaymentRequestRepository paymentRequestRepository;
     private PaymentRequestAccessValidator paymentRequestAccessValidator;

     @Override
     @Transactional
     public ResponseEntity<ApiResponse<Map<String, String>>> validateTransaction(String refId){
          String email = EmailExtractorUtil.getEmailFromJwt();

          paymentRequestAccessValidator.isPaymentRequestAccessValid(email,refId);

          //user and paymentRequest exists for sure (verified by above method call)
          User user =userRepository.findByEmail(email).get();
          PaymentRequest paymentRequest = paymentRequestRepository.findByRefId(refId).get();

          User merchant = userRepository.findByEmail("merchant@gmail.com").get();


          if(paymentRequest.getStatus().equals(Status.SUCCESS)){
               throw new InvalidTransactionRequestException("Transaction with the provided refId has already been completed");
          }

          if(!paymentRequest.getStatus().equals(Status.AUTHENTICATED)){
               throw new InvalidTransactionRequestException("Authentication Required");
          }

          if (paymentRequest.getAmount() <= 0) {
               paymentRequest.setStatus(Status.FAILED);
               paymentRequestRepository.save(paymentRequest);
               throw new InvalidTransactionRequestException("Invalid Amount: Rs." + paymentRequest.getAmount());
          }

          double newUserBalance = user.getBalance() - paymentRequest.getAmount();
          if (newUserBalance < 0) {
               paymentRequest.setStatus(Status.FAILED);
               paymentRequestRepository.save(paymentRequest);
               throw new InvalidTransactionRequestException("Insufficient balance: Rs." + user.getBalance());
          }

          paymentRequest.setStatus(Status.SUCCESS);

          user.setBalance(newUserBalance);
          userRepository.save(user);

          paymentRequest.setDebitStatus(Status.SUCCESS);


          double newMerchantBalance = merchant.getBalance() + paymentRequest.getAmount();

          // Credit process
          merchant.setBalance(newMerchantBalance);
          userRepository.save(merchant);
          paymentRequest.setCreditStatus(Status.SUCCESS);
          paymentRequest.setStatus(Status.SUCCESS);

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
