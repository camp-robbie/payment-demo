package com.bootcamp.paymentdemo.dto;

public record CreatePaymentResponse(boolean success, long paymentId, String status) {
}
