package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.WebhookDeliveryLog;
import org.springframework.data.repository.CrudRepository;

public interface WebhookDeliveryLogRepository extends CrudRepository<WebhookDeliveryLog , Long> {
}
