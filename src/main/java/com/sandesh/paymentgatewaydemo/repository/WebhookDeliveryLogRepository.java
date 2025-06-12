package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.WebhookDeliveryLog;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WebhookDeliveryLogRepository extends CrudRepository<WebhookDeliveryLog , Long> {
    List<WebhookDeliveryLog> findByNextRetryAtBefore(LocalDateTime now);
}
