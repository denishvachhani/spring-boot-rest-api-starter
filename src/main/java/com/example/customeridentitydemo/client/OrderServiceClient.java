package com.example.customeridentitydemo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "${order-service.url}")
public interface OrderServiceClient {

    @GetMapping("/orders/customer/{customerId}")
    List<OrderResponseDTO> getOrdersByCustomerId(@PathVariable("customerId") Long customerId);

}
