package com.sandesh.paymentgatewaydemo.entity;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("payment_request")
public class PaymentRequest {

    @Id
    private Long id;

    private AppId appId;

    private Double amount;

    private String particular;

    private String refId;

    private String remarks;

    private Status debitStatus;

    private Status creditStatus;

    private Status status;

    private Long userId;
}