package com.bootcamp.paymentdemo.controller;

import com.bootcamp.paymentdemo.application.OrderApplicationService;
import com.bootcamp.paymentdemo.dto.CreateOrderRequest;
import com.bootcamp.paymentdemo.dto.CreateOrderResponse;
import com.bootcamp.paymentdemo.dto.OrderSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request, Principal principal) {
        try {
            CreateOrderResponse response = orderService.createOrder(principal.getName(), request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> listOrders(Principal principal) {
        return ResponseEntity.ok(orderService.listOrders(principal.getName()));
    }
}
