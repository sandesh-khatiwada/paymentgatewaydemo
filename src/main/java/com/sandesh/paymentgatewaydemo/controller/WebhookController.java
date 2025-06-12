package com.sandesh.paymentgatewaydemo.controller;

import com.sandesh.paymentgatewaydemo.dto.WebhookConfigDTO;
import com.sandesh.paymentgatewaydemo.dto.WebhookConfigRequest;
import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.service.webhook.WebhookService;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@AllArgsConstructor
public class WebhookController {

    private WebhookService webhookService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<WebhookConfigDTO>> registerWebhook(@RequestBody WebhookConfigRequest request, @RequestHeader String appId){
        return webhookService.registerWebhook(request,appId);
    }

}
