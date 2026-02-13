package com.bootcamp.paymentdemo.repository;

import com.bootcamp.paymentdemo.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
