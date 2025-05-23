package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.Otp;
import com.sandesh.paymentgatewaydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByUserEmailAndHasExpiredFalse(String email);
    Optional<Otp> findByUser(User user);
}
