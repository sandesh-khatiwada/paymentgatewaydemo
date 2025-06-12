package com.sandesh.paymentgatewaydemo.service.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.paymentgatewaydemo.dto.WebhookConfigRequest;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;




public interface WebhookService {

    ResponseEntity<ApiResponse<WebhookConfig>> registerWebhook(WebhookConfigRequest request, String appId);
    ResponseEntity<ApiResponse<WebhookConfig>> getWebhookByAppId(AppId appId);

    void triggerWebhook(PaymentRequest paymentRequest, String eventType, ObjectMapper objectMapper);

}
