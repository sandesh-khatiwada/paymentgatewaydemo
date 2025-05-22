package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.OtpRequestDTO;
import com.sandesh.paymentgatewaydemo.service.OtpService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/otp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> sendOtp() {
        return otpService.sendOtp();
    }

    @PostMapping("/otp/verification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpRequestDTO otpRequestDTO) {
        return otpService.verifyOtp(otpRequestDTO.getOtp());
    }


}
