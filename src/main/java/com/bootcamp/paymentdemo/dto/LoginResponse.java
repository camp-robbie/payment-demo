package com.bootcamp.paymentdemo.dto;

public record LoginResponse(boolean success, String email, String message) {
}
