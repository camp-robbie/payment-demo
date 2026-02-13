package com.bootcamp.paymentdemo.dto;

public record OrderSummaryResponse(
    long orderId,
    String orderNumber,
    long totalAmount,
    long usedPoints,
    long finalAmount,
    long earnedPoints,
    String currency,
    String status,
    String createdAt
) {
}
