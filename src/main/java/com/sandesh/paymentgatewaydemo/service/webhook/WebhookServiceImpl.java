package com.sandesh.paymentgatewaydemo.service.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.paymentgatewaydemo.dto.PaymentRequestDTO;
import com.sandesh.paymentgatewaydemo.dto.WebhookConfigRequest;
import com.sandesh.paymentgatewaydemo.dto.WebhookPayloadDTO;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import com.sandesh.paymentgatewaydemo.entity.WebhookDeliveryLog;
import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.repository.WebhookConfigRepository;
import com.sandesh.paymentgatewaydemo.repository.WebhookDeliveryLogRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WebhookServiceImpl implements WebhookService{
    private WebhookConfigRepository webhookConfigRepository;
    private WebhookDeliveryLogRepository webhookDeliveryLogRepository;
//    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;


    @Override
    public ResponseEntity<ApiResponse<WebhookConfig>> registerWebhook(@Valid WebhookConfigRequest request, String appId) {


        if (request.getUrl() == null|| request.getEvents() == null) {
            throw new IllegalArgumentException("Webhook URL and event type are required");
        }


        validateUrl(request.getUrl());

        // Convert String to AppId enum
//        AppId applicationId;
//        try {
//            applicationId = AppId.valueOf(appId);
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid App Id: " + appId);
//        }

        AppId applicationId = AppId.fromName(appId);

        Boolean isRegistered =  webhookConfigRepository.existsByAppId(applicationId);

        if(isRegistered){
            throw new InvalidAccessException("The webhook for the provided application is already registered");
        }

        WebhookConfig webhookConfig = new WebhookConfig();

        webhookConfig.setUrl(request.getUrl());
        webhookConfig.setAppId(applicationId);
        webhookConfig.setEvents(String.join(",", request.getEvents()));
        webhookConfig.setSecretKey(generateSecretKey());
        webhookConfig.setEnabled(true);
        webhookConfig.setCreatedAt(LocalDateTime.now());
        WebhookConfig savedWebhookConfig = webhookConfigRepository.save(webhookConfig);

        ApiResponse<WebhookConfig> response = new ApiResponse<>(
                HttpStatus.OK,
                "Webhook registered successfully",
                savedWebhookConfig
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse<WebhookConfig>> getWebhookByAppId(AppId appId) {


        return null;
    }

    @Override
    public void triggerWebhook(PaymentRequest paymentRequest, String eventType, ObjectMapper objectMapper){
        WebhookConfig config = webhookConfigRepository.findByAppIdAndEnabledTrue(paymentRequest.getAppId());

        if(config.getEvents().contains(eventType)){
            sendWebhook(config,paymentRequest,eventType, objectMapper);
        }


    }

    private void sendWebhook(WebhookConfig config, PaymentRequest paymentRequest, String eventType, ObjectMapper objectMapper){
        try{
        WebhookPayloadDTO webhookPayloadDTO= buildPayload(paymentRequest, eventType, config.getSecretKey(), objectMapper);

        String payloadJson = objectMapper.writeValueAsString(webhookPayloadDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Webhook-Signature", webhookPayloadDTO.getSignature());
        HttpEntity<String> request = new HttpEntity<>(payloadJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(config.getUrl(), request, String.class);

        // Log delivery
        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.setWebhookId(config.getId());
        log.setEventType(eventType);
        log.setPayload(payloadJson);
        log.setStatusCode(response.getStatusCode());
        log.setResponseMessage(response.getBody());
        log.setAttemptCount(1);
        webhookDeliveryLogRepository.save(log);

        if (response.getStatusCode() != HttpStatusCode.valueOf(200)) {
//            scheduleRetry(log);
        }

    } catch (Exception e) {
        // Log error and schedule retry
        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.setWebhookId(config.getId());
        log.setEventType(eventType);
        log.setPayload("Error: " + e.getMessage());
        log.setStatusCode(HttpStatusCode.valueOf(500));
        log.setResponseMessage(e.getMessage());
        log.setAttemptCount(1);
        webhookDeliveryLogRepository.save(log);
        scheduleRetry(log);
    }


    }


    private WebhookPayloadDTO buildPayload(PaymentRequest paymentRequest, String eventType, String secretKey, ObjectMapper objectMapper){
        WebhookPayloadDTO webhookPayloadDTO= new WebhookPayloadDTO();

        webhookPayloadDTO.setAppId(paymentRequest.getAppId());
        webhookPayloadDTO.setAmount(paymentRequest.getAmount());
        webhookPayloadDTO.setRefId(paymentRequest.getRefId());
        webhookPayloadDTO.setStatus(paymentRequest.getStatus());
        webhookPayloadDTO.setTimeStamp(LocalDateTime.now());
        webhookPayloadDTO.setEventType(eventType);

        String signature = generateSignature(webhookPayloadDTO, secretKey, objectMapper);
        webhookPayloadDTO.setSignature(signature);

        return webhookPayloadDTO;
    }

    private void scheduleRetry(WebhookDeliveryLog log) {
        int maxAttempts = 5;
        if (log.getAttemptCount() < maxAttempts) {
            // Exponential backoff: 1 min, 2 min, 4 min, 8 min, 16 min
            long delayMinutes = (long) Math.pow(2, log.getAttemptCount());
            log.setNextRetryAt(LocalDateTime.now());
            webhookDeliveryLogRepository.save(log);
        }
    }




    private void validateUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid webhook URL: " + e.getMessage());
        }
    }

    private String generateSecretKey(){
        return UUID.randomUUID().toString();
    }

    private String generateSignature(WebhookPayloadDTO payload, String secretKey, ObjectMapper objectMapper) {
        try {
            String data = objectMapper.writeValueAsString(payload);
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
        }
    }
}
