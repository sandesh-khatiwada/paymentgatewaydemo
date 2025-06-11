package com.sandesh.paymentgatewaydemo.dto;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookPayloadDTO {

    private String eventType;

    private String timeStamp;

    private AppId appId;

    private String refId;

    private Double amount;

    private Status status;


    private String signature;  // HMAC-SHA256 signature for security

}
