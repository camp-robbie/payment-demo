package com.bootcamp.paymentdemo.repository;

import com.bootcamp.paymentdemo.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserEmailOrderByCreatedAtDesc(String email);
    Optional<OrderEntity> findByIdAndUserEmail(Long id, String email);
}
