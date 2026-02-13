package com.bootcamp.paymentdemo.dto;

public record ConfirmPaymentResponse(boolean success, long orderId, String status) {
}
