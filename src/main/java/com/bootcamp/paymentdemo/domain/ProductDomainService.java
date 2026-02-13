package com.bootcamp.paymentdemo.domain;

import com.bootcamp.paymentdemo.entity.ProductEntity;
import com.bootcamp.paymentdemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDomainService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductEntity> listProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductEntity getById(long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));
    }
}
