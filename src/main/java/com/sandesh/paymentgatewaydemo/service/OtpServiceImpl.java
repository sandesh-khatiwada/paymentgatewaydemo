
package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.entity.Otp;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.exception.InvalidOTPException;
import com.sandesh.paymentgatewaydemo.repository.OtpRepository;
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


@Service
@AllArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final JavaMailSender mailSender;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse<Void>> sendOtp() {

        String email = getEmailFromJwt();


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        String otpValue = generateOtp();


        Otp otp = new Otp();
        otp.setOtp(otpValue);
        otp.setHasExpired(false);
        otp.setUser(user);

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
    public ResponseEntity<ApiResponse<Void>> verifyOtp(String otp) {

        if (otp == null || otp.trim().isEmpty()) {
            throw new InvalidOTPException("OTP cannot be null or empty");
        }
        if (!otp.matches("\\d{6}")) {
            throw new InvalidOTPException("OTP must be exactly 6 digits");
        }

        String email = getEmailFromJwt();


        Otp storedOtp = otpRepository.findByUserEmailAndHasExpiredFalse(email)
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
