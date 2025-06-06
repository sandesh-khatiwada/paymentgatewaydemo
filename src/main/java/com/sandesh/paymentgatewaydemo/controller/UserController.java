package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.UserDTO;
import com.sandesh.paymentgatewaydemo.service.UserService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    //test

    UserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<UserDTO>> getUser(){
        return userService.getUserDetails();
    }
}
