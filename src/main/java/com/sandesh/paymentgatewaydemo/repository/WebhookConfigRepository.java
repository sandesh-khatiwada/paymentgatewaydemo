package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.WebhookConfig;
import com.sandesh.paymentgatewaydemo.enums.AppId;
import org.springframework.data.repository.CrudRepository;

public interface WebhookConfigRepository extends CrudRepository<WebhookConfig, Long> {

    Boolean existsByAppId(AppId appId);
    WebhookConfig findByAppIdAndEnabledTrue(AppId appId);
}
