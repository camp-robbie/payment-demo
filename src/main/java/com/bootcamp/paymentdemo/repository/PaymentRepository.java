package com.bootcamp.paymentdemo.repository;

import com.bootcamp.paymentdemo.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByIdAndUserEmail(Long id, String email);
}
