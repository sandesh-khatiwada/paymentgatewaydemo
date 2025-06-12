package com.sandesh.paymentgatewaydemo.service.auth;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.enums.Status;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.service.cache.PaymentCacheService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import com.sandesh.paymentgatewaydemo.util.CacheInspectorUtil;
import com.sandesh.paymentgatewaydemo.util.JwtUtil;
import com.sandesh.paymentgatewaydemo.dto.LoginRequest;
import com.sandesh.paymentgatewaydemo.dto.LoginResponse;
import com.sandesh.paymentgatewaydemo.entity.User;
import com.sandesh.paymentgatewaydemo.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PaymentCacheService paymentCacheService;
    private final CacheInspectorUtil cacheInspectorUtil;

    public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest loginRequest) {

        String refId = loginRequest.getRefId();

        if(refId.isEmpty()){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);

        if (paymentRequest == null) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        if (!paymentRequest.getStatus().equals(Status.PENDING)) {
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsernameOrEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(), loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        paymentRequest.setUserId(user.getId());
        paymentCacheService.cachePaymentRequest(paymentRequest);

        cacheInspectorUtil.inspectPendingPaymentsCache();


        LoginResponse loginResponse= new LoginResponse(jwt, user.getUsername(), user.getEmail());

        ApiResponse<LoginResponse> response = new ApiResponse<>(
                HttpStatus.OK,
                "Login successful",
                loginResponse
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse<String>> validateAccess( String refId) {
        // Validate refId and transaction
        if(refId.isEmpty()){
            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");
        }

        PaymentRequest paymentRequest = paymentCacheService.getPendingPayment(refId);
        if (paymentRequest == null) {

            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        if (!paymentRequest.getStatus().equals(Status.PENDING)) {

            throw new InvalidAccessException("Invalid transaction reference or transaction request has been expired");

        }

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK,
                "Valid transaction, proceed to login",
                null
        ));
    }


}