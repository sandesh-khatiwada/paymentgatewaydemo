
package com.sandesh.paymentgatewaydemo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequestDTO {
    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "Transaction reference is required")
    private String refId;
}
