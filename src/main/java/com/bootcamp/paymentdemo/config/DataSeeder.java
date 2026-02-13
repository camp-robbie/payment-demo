package com.bootcamp.paymentdemo.config;

import com.bootcamp.paymentdemo.entity.ProductEntity;
import com.bootcamp.paymentdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        List<ProductEntity> products = List.of(
            ProductEntity.create("프리미엄 무선 이어폰", 89000L, 50),
            ProductEntity.create("스마트 워치", 299000L, 30),
            ProductEntity.create("게이밍 키보드", 129000L, 40),
            ProductEntity.create("4K 모니터", 499000L, 15),
            ProductEntity.create("USB-C 허브", 39000L, 100)
        );

        productRepository.saveAll(products);
    }
}
