package com.bootcamp.paymentdemo.support.fixture;

import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.ProductEntity;
import com.bootcamp.paymentdemo.entity.UserEntity;

import java.util.List;

public class OrderFixture {

    public static OrderEntity anOrder(UserEntity user) {
        return OrderEntity.create(user);
    }

    public static OrderEntity anOrder(UserEntity user, List<ProductEntity> products) {
        OrderEntity order = OrderEntity.create(user);
        for (ProductEntity product : products) {
            order.addItem(product, 1);
        }
        return order;
    }

    public static OrderEntity anOrder(UserEntity user, ProductEntity product, int quantity) {
        OrderEntity order = OrderEntity.create(user);
        order.addItem(product, quantity);
        return order;
    }
}
