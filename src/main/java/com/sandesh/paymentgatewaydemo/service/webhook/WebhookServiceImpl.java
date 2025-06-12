package com.sandesh.paymentgatewaydemo.service.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.paymentgatewaydemo.dto.WebhookConfigDTO;
import com.sandesh.paymentgatewaydemo.dto.WebhookConfigRequest;
import com.sandesh.paymentgatewaydemo.dto.WebhookPayloadDTO;
import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import com.sandesh.paymentgatewaydemo.entity.WebhookDeliveryLog;
import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.exception.InvalidAccessException;
import com.sandesh.paymentgatewaydemo.mapper.WebhookConfigMapper;
import com.sandesh.paymentgatewaydemo.repository.WebhookConfigRepository;
import com.sandesh.paymentgatewaydemo.repository.WebhookDeliveryLogRepository;
import com.sandesh.paymentgatewaydemo.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WebhookServiceImpl implements WebhookService{
    private final WebhookConfigRepository webhookConfigRepository;
    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final WebhookConfigMapper webhookConfigMapper;


    @Override
    public ResponseEntity<ApiResponse<WebhookConfigDTO>> registerWebhook(@Valid WebhookConfigRequest request, String appId) {


        if (request.getUrl() == null|| request.getEvents() == null) {
            throw new IllegalArgumentException("Webhook URL and event type are required");
        }

        validateUrl(request.getUrl());

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

        WebhookConfigDTO webhookConfigDTO = webhookConfigMapper.toDTO(savedWebhookConfig);

        ApiResponse<WebhookConfigDTO> response = new ApiResponse<>(
                HttpStatus.OK,
                "Webhook registered successfully",
                webhookConfigDTO
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    @Async
    public void triggerWebhook(PaymentRequest paymentRequest, String eventType, String statusMessage){
        WebhookConfig config = webhookConfigRepository.findByAppIdAndEnabledTrue(paymentRequest.getAppId());

        if(config.getEvents().contains(eventType)){
            sendWebhook(config,paymentRequest,eventType,statusMessage );
        }

    }

    @Override
    public void scheduleRetry(WebhookDeliveryLog log) {
        int maxAttempts = 5;
        if (log.getAttemptCount() < maxAttempts) {
            // Exponential: 1 min, 2 min, 4 min, 8 min, 16 min
            long delayMinutes = (long) Math.pow(2, log.getAttemptCount());
            log.setNextRetryAt(LocalDateTime.now().plusMinutes(delayMinutes));
            webhookDeliveryLogRepository.save(log);
        }
    }


    private void sendWebhook(WebhookConfig config, PaymentRequest paymentRequest, String eventType, String statusMessage){

        String payloadJson="";
        try{
        WebhookPayloadDTO webhookPayloadDTO= buildPayload(paymentRequest, eventType, config.getSecretKey(), statusMessage);

        payloadJson = objectMapper.writeValueAsString(webhookPayloadDTO);
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
        log.setStatusCode(response.getStatusCode().value());
        log.setResponseMessage(response.getBody());
        log.setAttemptCount(1);
        log.setCreatedAt(LocalDateTime.now());
        webhookDeliveryLogRepository.save(log);

        if (response.getStatusCode() != HttpStatusCode.valueOf(200)) {
            System.out.println("Did not get 200 response, Retry scheduled...");
            scheduleRetry(log);
        }

    } catch (Exception e) {
        // Log error and schedule retry
        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.setWebhookId(config.getId());
        log.setEventType(eventType);
        log.setPayload(payloadJson);
        log.setStatusCode(500);
        log.setResponseMessage(e.getMessage());
        log.setAttemptCount(1);
        log.setCreatedAt(LocalDateTime.now());
        webhookDeliveryLogRepository.save(log);
        scheduleRetry(log);
    }


    }


    private WebhookPayloadDTO buildPayload(PaymentRequest paymentRequest, String eventType, String secretKey, String statusMessage){
        WebhookPayloadDTO webhookPayloadDTO= new WebhookPayloadDTO();

        webhookPayloadDTO.setAppId(paymentRequest.getAppId());
        webhookPayloadDTO.setAmount(paymentRequest.getAmount());
        webhookPayloadDTO.setRefId(paymentRequest.getRefId());
        webhookPayloadDTO.setStatus(paymentRequest.getStatus());
        webhookPayloadDTO.setTimeStamp(LocalDateTime.now().toString());
        webhookPayloadDTO.setEventType(eventType);
        webhookPayloadDTO.setStatusMessage(statusMessage);

        String signature = generateSignature(webhookPayloadDTO, secretKey);
        webhookPayloadDTO.setSignature(signature);

        return webhookPayloadDTO;
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

    private String generateSignature(WebhookPayloadDTO payload, String secretKey) {
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
