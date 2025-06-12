package com.sandesh.paymentgatewaydemo.service.webhook;

import com.sandesh.paymentgatewaydemo.dto.WebhookConfigDTO;
import com.sandesh.paymentgatewaydemo.dto.WebhookConfigRequest;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.WebhookDeliveryLog;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import org.springframework.http.ResponseEntity;




public interface WebhookService {

    ResponseEntity<ApiResponse<WebhookConfigDTO>> registerWebhook(WebhookConfigRequest request, String appId);
    void triggerWebhook(PaymentRequest paymentRequest, String eventType, String statusMessage);
    void scheduleRetry(WebhookDeliveryLog log);
}
