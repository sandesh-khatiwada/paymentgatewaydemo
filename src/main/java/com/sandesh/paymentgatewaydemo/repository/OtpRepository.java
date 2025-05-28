package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.Otp;
import com.sandesh.paymentgatewaydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {

    Optional<Otp> findByUserEmailAndHasExpiredTrue(String email);
    Optional<Otp> findByUserEmail(String email);

    Optional<Otp> findByUser(User user);

    @Query("SELECT o FROM Otp o " +
            "JOIN o.user u " +
            "JOIN o.paymentRequest pr " +
            "WHERE u.email = :email " +
            "AND pr.refId = :refId " +
            "AND o.hasExpired = false")
    Optional<Otp> findByUserEmailAndPaymentRequestRefIdAndHasExpiredFalse(
            @Param("email") String email,
            @Param("refId") String refId);
}
