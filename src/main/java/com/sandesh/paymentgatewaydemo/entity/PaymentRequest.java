
package com.sandesh.paymentgatewaydemo.entity;

import com.sandesh.paymentgatewaydemo.enums.AppId;
import com.sandesh.paymentgatewaydemo.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_request")
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

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
}
