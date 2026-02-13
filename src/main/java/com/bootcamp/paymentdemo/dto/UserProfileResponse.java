package com.bootcamp.paymentdemo.dto;

public record UserProfileResponse(
    String customerUid,
    String email,
    String name,
    String phone,
    long pointBalance
) {
}
