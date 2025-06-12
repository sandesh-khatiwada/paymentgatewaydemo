package com.sandesh.paymentgatewaydemo.service.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandesh.paymentgatewaydemo.dto.WebhookPayloadDTO;
import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import com.sandesh.paymentgatewaydemo.entity.WebhookDeliveryLog;
import com.sandesh.paymentgatewaydemo.repository.WebhookConfigRepository;
import com.sandesh.paymentgatewaydemo.repository.WebhookDeliveryLogRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class WebhookRetryServiceImpl implements WebhookRetryService{

    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final WebhookService webhookService;
    private final WebhookConfigRepository webhookConfigRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private  final Logger logger = LoggerFactory.getLogger(WebhookRetryServiceImpl.class);




    @Override
    @Scheduled(fixedRate = 60000) // Runs every minute

    public void retryFailedWebhooks() {
        LocalDateTime now = LocalDateTime.now();
        List<WebhookDeliveryLog> logs = webhookDeliveryLogRepository.findByNextRetryAtBefore(now);

        for (WebhookDeliveryLog log : logs) {
            logger.info(log.toString());
            WebhookConfig config = webhookConfigRepository.findById(log.getWebhookId())
                    .orElse(null);
            if (config != null && config.isEnabled()) {
                retryWebhook(config, log);
            }
        }
    }


    private void retryWebhook(WebhookConfig config, WebhookDeliveryLog log){

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            WebhookPayloadDTO payload = objectMapper.readValue(log.getPayload(), WebhookPayloadDTO.class);
            headers.set("X-Webhook-Signature", payload.getSignature());
            HttpEntity<String> request = new HttpEntity<>(log.getPayload(), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(config.getUrl(), request, String.class);


            log.setStatusCode(response.getStatusCodeValue());
            log.setResponseMessage(response.getBody());
            log.setAttemptCount(log.getAttemptCount() + 1);
            if (response.getStatusCodeValue() != 200) {
                webhookService.scheduleRetry(log);
            }else{
                log.setNextRetryAt(null);
            }
            webhookDeliveryLogRepository.save(log);

        }catch(Exception e){
            log.setStatusCode(500);
            log.setResponseMessage(e.getMessage());
            log.setAttemptCount(log.getAttemptCount() + 1);
            webhookService.scheduleRetry(log);
            webhookDeliveryLogRepository.save(log);
        }
    }
}


