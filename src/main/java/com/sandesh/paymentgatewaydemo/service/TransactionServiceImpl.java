package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.Transaction;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidPaymentRequestException;
import com.sandesh.paymentgatewaydemo.exception.InvalidTransactionRequestException;
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
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, String>>> addTransaction(String refId) {
        // Extract email from JWT token
        String email = getEmailFromJwt();

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // Find payment request by refId
        PaymentRequest paymentRequest = paymentRequestRepository.findByRefId(refId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction request not found for refId: " + refId));

        // Validate payment request
        if (paymentRequest.getAmount() <= 0) {
            paymentRequest.setStatus(Status.FAILED);
            paymentRequestRepository.save(paymentRequest);
            throw new InvalidTransactionRequestException("Invalid Amount: " + paymentRequest.getAmount());
        }

        if (paymentRequest.getParticular() == null || paymentRequest.getParticular().isEmpty()) {
            paymentRequest.setStatus(Status.FAILED);
            paymentRequestRepository.save(paymentRequest);
            throw new InvalidTransactionRequestException("Invalid Particular: " + paymentRequest.getParticular());
        }

        // Check sufficient balance
        double newBalance = user.getBalance() - paymentRequest.getAmount();
        if (newBalance < 0) {
            paymentRequest.setStatus(Status.FAILED);
            paymentRequestRepository.save(paymentRequest);
            throw new InvalidTransactionRequestException("Insufficient balance: " + user.getBalance());
        }

        // Update user balance
        user.setBalance(newBalance);
        userRepository.save(user);

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setDebitStatus(Status.SUCCESS);

        transaction.setAppId(paymentRequest.getAppId());
        transaction.setRefId(paymentRequest.getRefId());
        transaction.setUser(user);

        transaction.setCreditStatus(Status.SUCCESS);
        transaction.setStatus(Status.SUCCESS);
        transaction.setAmount(paymentRequest.getAmount());

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Update payment request status
        paymentRequest.setStatus(Status.SUCCESS);
        paymentRequestRepository.save(paymentRequest);

        // Prepare response
        Map<String, String> responseData = new HashMap<>();
        responseData.put("transactionId", savedTransaction.getId().toString());
        responseData.put("status", savedTransaction.getStatus().toString());
        responseData.put("refId", savedTransaction.getRefId());

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
