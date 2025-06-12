package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.AccessRequestDTO;
import com.sandesh.paymentgatewaydemo.dto.OtpRequestDTO;
import com.sandesh.paymentgatewaydemo.service.auth.OtpService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.POST, RequestMethod.OPTIONS})
public class OtpController {
    private final OtpService otpService;


    @GetMapping("/otp/{refId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> validateAccess(@PathVariable String refId){
        return otpService.validateAccess(refId);
    }

    @PostMapping("/otp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@RequestBody AccessRequestDTO accessRequestDTO) {
        return otpService.sendOtp(accessRequestDTO.getRefId());
    }

    @PostMapping("/otp/verification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody OtpRequestDTO otpRequestDTO) {
        return otpService.verifyOtp(otpRequestDTO.getOtp(), otpRequestDTO.getRefId());
    }


}
