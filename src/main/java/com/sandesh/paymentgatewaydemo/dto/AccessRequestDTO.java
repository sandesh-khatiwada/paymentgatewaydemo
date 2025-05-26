package com.sandesh.paymentgatewaydemo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccessRequestDTO {
    @NotBlank(message = "Transaction reference is required")
    private String refId;
}
