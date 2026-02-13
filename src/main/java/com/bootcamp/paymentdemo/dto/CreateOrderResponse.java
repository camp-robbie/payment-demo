package com.bootcamp.paymentdemo.dto;

public record CreateOrderResponse(long orderId, long totalAmount, String orderNumber) {
}
