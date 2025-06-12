package com.sandesh.paymentgatewaydemo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sandesh.paymentgatewaydemo.enums.AppId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("webhook_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookConfig {

    @Id
    @Column("webhook_config_id")
    private Long id;

    @Column("app_id")
    private AppId appId;

    private String url;

    private String events;  //subscribed events: like payment.success, payment.failure

    @Column("secret_key")
    private String secretKey; //Secret for signing webhook payloads

    private boolean enabled = true;  //enabled by default

    @Column("created_at")
    private LocalDateTime createdAt;

}
