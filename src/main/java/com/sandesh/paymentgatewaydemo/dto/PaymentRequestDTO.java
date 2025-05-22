package com.sandesh.paymentgatewaydemo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

//    @NotBlank(message = "Application must not be blank")
//    @Pattern(regexp = "^(eCom1|eCom2|eCom3)$", message = "Application must be one of: eCom1, eCom2, eCom3")
    private String application;

//    @NotNull(message = "Amount must not be null")
//    @Positive(message = "Amount must be positive")
    private Double amount;

//    @NotBlank(message = "Particular must not be blank")
//    @Size(max = 255, message = "Particular must not exceed 255 characters")
    private String particular;

//    @NotBlank(message = "Reference ID must not be blank")
//    @Size(max = 50, message = "Reference ID must not exceed 50 characters")
    private String refId;

//    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;
}