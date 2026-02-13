package com.bootcamp.paymentdemo.dto;

public record CreatePaymentRequest(Long orderId, Long totalAmount, Long pointsToUse) {
}
