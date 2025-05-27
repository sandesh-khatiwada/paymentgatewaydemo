package com.sandesh.paymentgatewaydemo.service;

import com.sandesh.paymentgatewaydemo.dto.UserDTO;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<ApiResponse<UserDTO>> getUserDetails();
}
