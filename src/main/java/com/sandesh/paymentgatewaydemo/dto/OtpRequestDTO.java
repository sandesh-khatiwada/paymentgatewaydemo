
package com.sandesh.paymentgatewaydemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpRequestDTO {
    @NotBlank(message = "OTP is required")
    private String otp;
}
