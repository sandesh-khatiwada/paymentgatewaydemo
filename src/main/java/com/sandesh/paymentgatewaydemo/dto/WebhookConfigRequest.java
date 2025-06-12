package com.sandesh.paymentgatewaydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookConfigRequest {

    @NonNull
    private String url;
    private List<String> events;
}
