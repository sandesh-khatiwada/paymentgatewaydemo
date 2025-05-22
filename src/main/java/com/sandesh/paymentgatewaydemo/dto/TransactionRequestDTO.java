package com.sandesh.paymentgatewaydemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequestDTO {

    @NotBlank(message = "refId is required")
    @NotNull
    private String refId;

}
