package com.sandesh.paymentgatewaydemo.util;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Boolean success;
    private HttpStatus status;
    private String code;
    private String message;
    private T data;
    private List<String> errors;


    private String timestamp;

    // Success response constructor
    public ApiResponse(HttpStatus status, String message, T data) {
        this.success = true;
        this.status = status;
        this.code="000";
        this.message = message;
        this.data = data;
        this.errors = null;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Success response without data
    public ApiResponse(HttpStatus status, String message) {
        this.success = true;
        this.status = status;
        this.code = "000";
        this.message = message;
        this.data = null;
        this.errors = null;
        this.timestamp = LocalDateTime.now().toString();
    }



    // Error response constructor
    public ApiResponse(HttpStatus status, String code, String message, List<String> errors) {
        this.success = false;
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = null;
        this.errors = errors;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Response with redirection URL for failure
    @SuppressWarnings("unchecked")
    public ApiResponse(HttpStatus status, String code, String message, String successURL, String failureURL, List<String> errors) {
        this.success = false;
        this.status = status;
        this.code = code;
        this.message = message;
        Map<String, String> redirectData = new HashMap<>();
        redirectData.put("successURL", successURL);
        redirectData.put("failureURL", failureURL);
        this.data = (T) redirectData;
        this.errors = errors;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Response with redirection URL for success
    public ApiResponse(HttpStatus status, String message, String successURL, String failureURL) {
        this.success = true;
        this.status = status;
        this.code = "000";
        this.message = message;
        Map<String, String> redirectData = new HashMap<>();
        redirectData.put("successURL", successURL);
        redirectData.put("failureURL", failureURL);
        this.data = (T) redirectData;
        this.errors = null;
        this.timestamp = LocalDateTime.now().toString();
    }
}