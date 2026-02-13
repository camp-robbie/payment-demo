package com.bootcamp.paymentdemo.application;

import com.bootcamp.paymentdemo.domain.ProductDomainService;
import com.bootcamp.paymentdemo.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductDomainService productDomainService;

    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        return productDomainService.listProducts().stream()
            .map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
            ))
            .toList();
    }
}
