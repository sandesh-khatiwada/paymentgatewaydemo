package com.sandesh.paymentgatewaydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching

public class PaymentgatewaydemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentgatewaydemoApplication.class, args);
	}

}
