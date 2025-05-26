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


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED,
                "AUTH001",
                "Invalid username or password",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "VAL001",
                "Invalid request body",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidKeyException(InvalidKeyException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "AUTH001 ",
                "Invalid JWT secret key configuration",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "E007",
                "Invalid request",
               Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidAccessException(InvalidAccessException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "E007 ",
                "Invalid Request",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidPaymentRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPaymentRequestException(InvalidPaymentRequestException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "E007 ",
                "Invalid Payment Request",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "E007",
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
                "E006",
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
                "AUTH001",
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
                "E007",
                "Validation Failed",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UnverifiedOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnverifiedOtpException(UnverifiedOtpException ex) {

        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST,
                "AUTH001",
                "OTP has not been verified.",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    //  RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND,
                "E404",
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
                "E500",
                "An unexpected error occurred",
                Collections.singletonList(ex.getMessage())
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
