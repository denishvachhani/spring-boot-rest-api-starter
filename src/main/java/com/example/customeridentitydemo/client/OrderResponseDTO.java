package com.example.customeridentitydemo.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal amount;
    private String orderStatus;
}
