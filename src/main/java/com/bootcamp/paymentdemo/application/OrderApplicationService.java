package com.bootcamp.paymentdemo.application;

import com.bootcamp.paymentdemo.domain.OrderDomainService;
import com.bootcamp.paymentdemo.domain.ProductDomainService;
import com.bootcamp.paymentdemo.domain.UserDomainService;
import com.bootcamp.paymentdemo.dto.CreateOrderRequest;
import com.bootcamp.paymentdemo.dto.CreateOrderResponse;
import com.bootcamp.paymentdemo.dto.OrderSummaryResponse;
import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.ProductEntity;
import com.bootcamp.paymentdemo.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderDomainService orderDomainService;
    private final ProductDomainService productDomainService;
    private final UserDomainService userDomainService;

    @Transactional
    public CreateOrderResponse createOrder(String email, CreateOrderRequest request) {
        UserEntity user = userDomainService.getByEmail(email);

        List<OrderDomainService.ProductSelection> selections = request.items().stream()
            .map(item -> {
                if (item.productId() == null || item.quantity() == null) {
                    throw new IllegalArgumentException("상품 ID와 수량은 필수입니다.");
                }
                ProductEntity product = productDomainService.getById(item.productId());
                return new OrderDomainService.ProductSelection(product, item.quantity());
            })
            .collect(Collectors.toList());

        OrderEntity order = orderDomainService.create(user, selections);
        return new CreateOrderResponse(order.getId(), order.getTotalAmount(), order.getOrderNumber());
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> listOrders(String email) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        return orderDomainService.listForUser(email).stream()
            .map(order -> new OrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getUsedPoints(),
                order.getFinalAmount(),
                order.getEarnedPoints(),
                order.getCurrency(),
                order.getStatus().name(),
                formatter.format(order.getCreatedAt())
            ))
            .toList();
    }
}
