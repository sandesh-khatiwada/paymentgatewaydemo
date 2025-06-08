package com.sandesh.paymentgatewaydemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("user")
public class User {

    @Id
    @Column("user_id")
    private Long id;

    private String username;

    private String email;

    private String password;

    private Double balance;

}