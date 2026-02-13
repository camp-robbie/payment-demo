package com.bootcamp.paymentdemo.support.fixture;

import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.PaymentEntity;
import com.bootcamp.paymentdemo.entity.UserEntity;

public class PaymentFixture {

    public static PaymentEntity aPayment(OrderEntity order, UserEntity user) {
        return PaymentEntity.create(order, user, order.getTotalAmount(), 0L);
    }

    public static PaymentEntity aPayment(OrderEntity order, UserEntity user, long pointsToUse) {
        return PaymentEntity.create(order, user, order.getTotalAmount(), pointsToUse);
    }
}
