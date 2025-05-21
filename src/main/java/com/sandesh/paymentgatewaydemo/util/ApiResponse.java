package com.sandesh.paymentgatewaydemo.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private HttpStatus status;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;

    // Success response constructor
    public ApiResponse(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = null;
        this.timestamp = LocalDateTime.now();
    }

    // Error response constructor
    public ApiResponse(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.data = null;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}