
package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.entity.Otp;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.exception.InvalidLoginException;
import com.sandesh.paymentgatewaydemo.exception.InvalidOTPException;
import com.sandesh.paymentgatewaydemo.exception.InvalidTransactionRequestException;
import com.sandesh.paymentgatewaydemo.repository.OtpRepository;
import com.sandesh.paymentgatewaydemo.repository.PaymentRequestRepository;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final JavaMailSender mailSender;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final PaymentRequestRepository paymentRequestRepository;

    public ResponseEntity<ApiResponse<String>> validateAccess( String refId) {
        // Validate refId and transaction
        if(refId.isEmpty()){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        Optional<PaymentRequest> optionalPaymentRequest = paymentRequestRepository.findByRefId(refId);
        if (!optionalPaymentRequest.isPresent()) {

            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        PaymentRequest paymentRequest = optionalPaymentRequest.get();
        if (!paymentRequest.getStatus().equals(Status.PENDING)) {

            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }


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

        Optional<PaymentRequest> optionalPaymentRequest = paymentRequestRepository.findByRefId(refId);
        if (!optionalPaymentRequest.isPresent()) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        PaymentRequest paymentRequest = optionalPaymentRequest.get();
        if (!paymentRequest.getStatus().equals(Status.PENDING)) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        String email = getEmailFromJwt();


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        String otpValue = generateOtp();


        Otp otp = new Otp();
        otp.setOtp(otpValue);
        otp.setHasExpired(false);
        otp.setUser(user);
        otp.setPaymentRequest(paymentRequest);


        //delete expired past OTP of the user
        Optional<Otp> existingOTP= otpRepository.findByUserEmailAndHasExpiredTrue(email);
        existingOTP.ifPresent(otpRepository::delete);



        try {
            otpRepository.save(otp);
        }catch (DataIntegrityViolationException exception){
            throw new DataIntegrityViolationException("OTP already sent to the requested email address");
        }
        //send otp to email
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

        Optional<PaymentRequest> optionalPaymentRequest = paymentRequestRepository.findByRefId(refId);
        if (!optionalPaymentRequest.isPresent()) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        PaymentRequest paymentRequest = optionalPaymentRequest.get();
        if (!paymentRequest.getStatus().equals(Status.PENDING)) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        if (otp == null || otp.trim().isEmpty()) {
            throw new InvalidOTPException("Invalid OTP");
        }
        if (!otp.matches("\\d{6}")) {
            throw new InvalidOTPException("Invalid OTP");
        }

        String email = getEmailFromJwt();


        Otp storedOtp = otpRepository.findByUserEmailAndPaymentRequestRefIdAndHasExpiredFalse(email,refId)
                .orElseThrow(() -> new IllegalArgumentException("No valid OTP found for email: " + email));


        if (storedOtp.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
//            storedOtp.setHasExpired(true);
//            otpRepository.save(storedOtp);
            otpRepository.delete(storedOtp);
            throw new IllegalArgumentException("OTP has expired for email: " + email);
        }

        if (!storedOtp.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP for email: " + email);
        }

        otpRepository.delete(storedOtp);


        paymentRequest.setStatus(Status.AUTHENTICATED);
        paymentRequestRepository.save(paymentRequest);


        ApiResponse<Void> response = new ApiResponse<>(
                HttpStatus.OK,
                "OTP verified successfully"
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
