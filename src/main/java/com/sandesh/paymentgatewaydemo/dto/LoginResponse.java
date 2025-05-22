package com.sandesh.paymentgatewaydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private String username;
    private String email;

}