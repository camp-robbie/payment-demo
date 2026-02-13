package com.bootcamp.paymentdemo.controller;

import com.bootcamp.paymentdemo.application.PaymentApplicationService;
import com.bootcamp.paymentdemo.dto.ConfirmPaymentResponse;
import com.bootcamp.paymentdemo.dto.CreatePaymentRequest;
import com.bootcamp.paymentdemo.dto.CreatePaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentApplicationService paymentService;

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request, Principal principal) {
        try {
            CreatePaymentResponse response = paymentService.createPayment(principal.getName(), request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable long paymentId, Principal principal) {
        try {
            ConfirmPaymentResponse response = paymentService.confirmPayment(principal.getName(), paymentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
