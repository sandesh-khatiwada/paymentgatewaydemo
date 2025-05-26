
package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.LoginRequest;
import com.sandesh.paymentgatewaydemo.dto.LoginResponse;
import com.sandesh.paymentgatewaydemo.service.AuthService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/login/{refId}")
    public ResponseEntity<ApiResponse<String>> validateAccess(@PathVariable String refId){
        return authService.validateAccess(refId);
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }


}

