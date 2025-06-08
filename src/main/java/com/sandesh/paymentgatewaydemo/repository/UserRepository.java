package com.sandesh.paymentgatewaydemo.repository;

import com.sandesh.paymentgatewaydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernameOrEmail(String username, String email);
//    Optional<User> findByEmail(String email);
}
