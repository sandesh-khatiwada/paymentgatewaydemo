package com.sandesh.paymentgatewaydemo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    private String application;

    private Double amount;

    private String particular;

    private String refId;

    private String remarks;
}