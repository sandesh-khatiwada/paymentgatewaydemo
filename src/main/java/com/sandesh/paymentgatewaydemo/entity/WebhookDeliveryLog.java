package com.sandesh.paymentgatewaydemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Data
@Table("webhook_delivery_log")
@AllArgsConstructor
@NoArgsConstructor
public class WebhookDeliveryLog {

    @Column("webhook_delivery_log_id")
    private Long id;

    @Column("webhook_id")
    private Long webhookId;

    @Column("event_type")
    private String eventType;   //type of event: eg: payment.success, payment.failure

    private String payload;    //Json payload sent to client

    @Column("status_code")
    private HttpStatusCode statusCode;

    @Column("response_msg")
    private String responseMessage;   //response msg provided by client

    @Column("attempt_count")
    private int attemptCount=0;    //number of attempts made

    @Column("next_retry_at")
    private LocalDateTime nextRetryAt;    //time for next retry if failed

    @Column("created_at")
    private LocalDateTime createdAt;

}
