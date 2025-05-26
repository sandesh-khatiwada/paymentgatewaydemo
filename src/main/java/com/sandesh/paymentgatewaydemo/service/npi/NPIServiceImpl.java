package com.sandesh.paymentgatewaydemo.service.npi;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.Transaction;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidTransactionRequestException;
import com.sandesh.paymentgatewaydemo.exception.UnverifiedOtpException;
import com.sandesh.paymentgatewaydemo.repository.OtpRepository;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.repository.TransactionRepository;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class NPIServiceImpl implements NPIService {

     private UserRepository userRepository;
     private PaymentRequestRepository paymentRequestRepository;

     @Override
     @Transactional
     public ResponseEntity<ApiResponse<Map<String, String>>> validateTransaction(String refId){
          String email = getEmailFromJwt();


          User user = userRepository.findByEmail(email)
                  .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

          PaymentRequest paymentRequest = paymentRequestRepository.findByRefId(refId)
                  .orElseThrow(() -> new IllegalArgumentException("Transaction request not found for refId: " + refId));


          if (paymentRequest.getAmount() <= 0) {
               paymentRequest.setStatus(Status.FAILED);
               paymentRequestRepository.save(paymentRequest);
               throw new InvalidTransactionRequestException("Invalid Amount: " + paymentRequest.getAmount());
          }

          double newBalance = user.getBalance() - paymentRequest.getAmount();
          if (newBalance < 0) {
               paymentRequest.setStatus(Status.FAILED);
               paymentRequestRepository.save(paymentRequest);
               throw new InvalidTransactionRequestException("Insufficient balance: " + user.getBalance());
          }

          paymentRequest.setStatus(Status.SUCCESS);

          user.setBalance(newBalance);
          userRepository.save(user);

          paymentRequest.setDebitStatus(Status.SUCCESS);


          // Credit process
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

     private String getEmailFromJwt() {
          Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
          if (principal instanceof UserDetails) {
               return ((UserDetails) principal).getUsername(); // Email is in 'sub'
          }
          throw new IllegalStateException("User not authenticated or invalid principal");
     }

}
