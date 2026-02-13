package com.bootcamp.paymentdemo.domain;

import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.ProductEntity;
import com.bootcamp.paymentdemo.entity.UserEntity;
import com.bootcamp.paymentdemo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderEntity create(UserEntity user, List<ProductSelection> selections) {
        if (selections == null || selections.isEmpty()) {
            throw new IllegalArgumentException("주문 아이템이 비어 있습니다.");
        }

        OrderEntity order = OrderEntity.create(user);
        for (ProductSelection selection : selections) {
            order.addItem(selection.product(), selection.quantity());
        }

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderEntity> listForUser(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public OrderEntity getForUser(String email, long orderId) {
        return orderRepository.findByIdAndUserEmail(orderId, email)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    }

    public record ProductSelection(ProductEntity product, int quantity) {
    }
}
