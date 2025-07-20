package com.example.customeridentitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories("com.example.customeridentitydemo.repository")
@EntityScan("com.example.customeridentitydemo.model")
public class CustomerIdentityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerIdentityDemoApplication.class, args);
    }
}