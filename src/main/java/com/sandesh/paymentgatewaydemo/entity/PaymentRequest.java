
package com.sandesh.paymentgatewaydemo.entity;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_request_id")
    private Long id;

    @Column(name = "app_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppId appId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "particular", nullable = false)
    private String particular;

    @Column(name = "ref_id", nullable = false)
    private String refId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "debit_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status debitStatus;

    @Column(name = "credit_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status creditStatus;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = true)
    private User user;

}
