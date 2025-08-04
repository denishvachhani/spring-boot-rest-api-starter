package com.example.customeridentitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CustomerIdentityDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerIdentityDemoApplication.class, args);
    }
}