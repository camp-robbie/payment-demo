package com.bootcamp.paymentdemo.support.fixture;

import com.bootcamp.paymentdemo.entity.ProductEntity;

public class ProductFixture {

    public static ProductEntity aProduct() {
        return ProductEntity.create("테스트 상품", 10000L, 100);
    }

    public static ProductEntity aProduct(String name, long price, int stock) {
        return ProductEntity.create(name, price, stock);
    }
}
