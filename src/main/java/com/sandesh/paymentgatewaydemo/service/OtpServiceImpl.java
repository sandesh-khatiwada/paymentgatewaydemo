
package com.sandesh.paymentgatewaydemo.service;


import com.sandesh.paymentgatewaydemo.entity.OtpEntry;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.exception.InvalidOTPException;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import com.sandesh.paymentgatewaydemo.util.CacheInspectorUtil;
import com.sandesh.paymentgatewaydemo.util.EmailExtractorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;



@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRequestAccessValidator paymentRequestAccessValidator;
    private final PaymentCacheService paymentCacheService;
    private final CacheInspectorUtil cacheInspectorUtil;


    public ResponseEntity<ApiResponse<String>> validateAccess( String refId) {
        // Validate refId and transaction
        if(refId.isEmpty()){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);


        if (paymentRequest==null) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }


        if (!paymentRequest.getStatus().equals(Status.PENDING)) {

            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        paymentRequestAccessValidator.isPaymentRequestAccessValid(EmailExtractorUtil.getEmailFromJwt(),refId);



        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK,
                "Valid transaction, proceed to otp page",
                null
        ));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> sendOtp(String refId) {

        System.out.println(refId);


        if(refId.isEmpty()){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);

        if(paymentRequest==null){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        if (!paymentRequest.getStatus().equals(Status.PENDING)) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        String email =EmailExtractorUtil.getEmailFromJwt();


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        paymentRequestAccessValidator.isPaymentRequestAccessValid(email,refId);

        String otpValue = generateOtp();

        // Clear any existing OTP for the user in the cache
        paymentCacheService.clearOtpByUserEmail(email);

        // Cache the new OTP
        paymentCacheService.cacheOtp(refId, otpValue);
        cacheInspectorUtil.inspectOtpsCache();

        sendEmail(email, otpValue);

        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "OTP sent to " + email
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ApiResponse<Void>> verifyOtp(String otp, String refId) {

        if(refId.isEmpty()){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }


        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);

        if(paymentRequest==null){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        if (!paymentRequest.getStatus().equals(Status.PENDING)) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        if (otp == null || otp.trim().isEmpty()) {
            throw new InvalidOTPException("Invalid OTP");
        }
        if (!otp.matches("\\d{6}")) {
            throw new InvalidOTPException("Invalid OTP");
        }

        String email = EmailExtractorUtil.getEmailFromJwt();


        paymentRequestAccessValidator.isPaymentRequestAccessValid(email,refId);



        OtpEntry otpEntry = paymentCacheService.getOtpEntry(refId);
        if (otpEntry == null) {
            throw new IllegalArgumentException("No valid OTP found for refId: " + refId);
        }

        if (otpEntry.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            paymentCacheService.clearOtp(refId);
            throw new IllegalArgumentException("OTP has expired");
        }

        if (!otpEntry.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        paymentCacheService.clearOtp(refId);
        cacheInspectorUtil.inspectOtpsCache();



        paymentRequest.setStatus(Status.AUTHENTICATED);


        paymentRequestRepository.save(paymentRequest);
//        paymentCacheService.clearPaymentRequest(refId);
//        cacheInspectorUtil.inspectPendingPaymentsCache();


        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "OTP verified successfully"
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void sendEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
        message.setFrom("khatiwadasandesh01@gmail.com");
        mailSender.send(message);
    }
}
