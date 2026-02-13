package com.bootcamp.paymentdemo.dto;

import java.util.List;

public record CreateOrderRequest(List<OrderItemRequest> items) {
}
