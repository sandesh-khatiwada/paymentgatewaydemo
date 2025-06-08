package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.PaymentRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRequestRepository extends CrudRepository<PaymentRequest, Long> {
    Optional<PaymentRequest> findByRefId(String refId);
}