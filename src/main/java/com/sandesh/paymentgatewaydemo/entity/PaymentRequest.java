package com.sandesh.paymentgatewaydemo.entity;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("payment_request")
public class PaymentRequest {

    @Id
    @Column("payment_request_id")
    private Long id;

    @Column("app_id")
    private AppId appId;

    private Double amount;

    private String particular;

    @Column("ref_id")
    private String refId;

    private String remarks;

    @Column("debit_status")
    private Status debitStatus;

    @Column("credit_status")
    private Status creditStatus;

    private Status status;

    @Column("user_id")
    private Long userId;
}