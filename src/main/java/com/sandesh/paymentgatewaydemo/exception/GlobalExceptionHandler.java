package com.sandesh.paymentgatewaydemo.exception;

import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    // BadCredentialsException (for invalid username/password)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    //  HttpMessageNotReadableException (for malformed/invalid request body)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "Invalid request body",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // InvalidKeyException (For invalid JWT secret key)
    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidKeyException(InvalidKeyException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Invalid JWT secret key configuration",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
               Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidPaymentRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPaymentRequestException(InvalidPaymentRequestException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "Invalid Payment Request",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "The request is not valid",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTransactionRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidTransactionRequestException(InvalidTransactionRequestException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "The transaction failed",
                "/success",
                "/failed",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidOTPException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOTPException(InvalidOTPException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "Invalid One Time Password",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UnverifiedOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnverifiedOtpException(UnverifiedOtpException ex) {

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "OTP has not been verified.",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    //  RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
