package com.sandesh.paymentgatewaydemo.entity;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transaction_id")
    private Long id;


    @Column(name = "app_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppId appId;

    @Column(name = "ref_id", nullable = false)
    private String refId;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "debit_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status debitStatus;

    @Column(name="credit_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private Status creditStatus;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}
