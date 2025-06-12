package com.sandesh.paymentgatewaydemo.dto;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookConfigDTO {

    private Long id;
    private AppId appId;
    private String url;
    private String events;
    private String secretKey;
    private boolean enabled = true;
    private String createdAt;
}
