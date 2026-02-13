package com.bootcamp.paymentdemo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(nullable = false)
    private long totalAmount;

    @Column(nullable = false)
    private long usedPoints;

    @Column(nullable = false)
    private long finalAmount;

    @Column(nullable = false)
    private long earnedPoints;

    @Column(nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItemEntity> items = new ArrayList<>();

    public static OrderEntity create(UserEntity user) {
        OrderEntity order = new OrderEntity();
        order.user = user;
        order.status = OrderStatus.PENDING;
        order.currency = "KRW";
        order.createdAt = Instant.now();
        order.usedPoints = 0L;
        order.earnedPoints = 0L;
        order.totalAmount = 0L;
        order.finalAmount = 0L;
        long now = Instant.now().toEpochMilli();
        int salt = (int) (Math.random() * 1000);
        order.orderNumber = "ORD-" + now + "-" + salt;
        return order;
    }

    public void addItem(ProductEntity product, int quantity) {
        product.decreaseStock(quantity);
        OrderItemEntity item = OrderItemEntity.create(this, product, quantity);
        items.add(item);
        recalculateTotals();
    }

    public void applyPoints(long points) {
        if (points < 0) {
            throw new IllegalArgumentException("사용 포인트는 0 이상이어야 합니다.");
        }
        if (points > totalAmount) {
            throw new IllegalArgumentException("사용 포인트가 주문 금액보다 클 수 없습니다.");
        }
        usedPoints = points;
        finalAmount = totalAmount - points;
    }

    public void ensurePayable(long expectedTotalAmount) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalArgumentException("결제 가능한 주문 상태가 아닙니다.");
        }
        if (totalAmount != expectedTotalAmount) {
            throw new IllegalArgumentException("주문 금액이 일치하지 않습니다.");
        }
    }

    public void markPaid() {
        status = OrderStatus.PAID;
    }

    public void markCancelled() {
        status = OrderStatus.CANCELLED;
    }

    private void recalculateTotals() {
        totalAmount = items.stream().mapToLong(OrderItemEntity::getSubtotal).sum();
        finalAmount = totalAmount - usedPoints;
    }
}
