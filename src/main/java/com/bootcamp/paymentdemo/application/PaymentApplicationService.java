package com.bootcamp.paymentdemo.application;

import com.bootcamp.paymentdemo.domain.OrderDomainService;
import com.bootcamp.paymentdemo.domain.PaymentDomainService;
import com.bootcamp.paymentdemo.domain.UserDomainService;
import com.bootcamp.paymentdemo.dto.ConfirmPaymentResponse;
import com.bootcamp.paymentdemo.dto.CreatePaymentRequest;
import com.bootcamp.paymentdemo.dto.CreatePaymentResponse;
import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.PaymentEntity;
import com.bootcamp.paymentdemo.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentApplicationService {

    private final PaymentDomainService paymentDomainService;
    private final OrderDomainService orderDomainService;
    private final UserDomainService userDomainService;

    @Transactional
    public CreatePaymentResponse createPayment(String email, CreatePaymentRequest request) {
        if (request.orderId() == null || request.totalAmount() == null) {
            throw new IllegalArgumentException("orderId와 totalAmount는 필수입니다.");
        }

        long pointsToUse = request.pointsToUse() == null ? 0L : request.pointsToUse();

        UserEntity user = userDomainService.getByEmail(email);
        OrderEntity order = orderDomainService.getForUser(email, request.orderId());

        PaymentEntity payment = paymentDomainService.createPayment(order, user, request.totalAmount(), pointsToUse);
        return new CreatePaymentResponse(true, payment.getId(), payment.getStatus().name());
    }

    @Transactional
    public ConfirmPaymentResponse confirmPayment(String email, long paymentId) {
        PaymentEntity payment = paymentDomainService.getForUser(email, paymentId);

        paymentDomainService.confirm(payment);

        return new ConfirmPaymentResponse(true, payment.getOrder().getId(), payment.getStatus().name());
    }

    @Transactional
    public ConfirmPaymentResponse cancelPayment(String email, long paymentId) {
        PaymentEntity payment = paymentDomainService.getForUser(email, paymentId);
        paymentDomainService.cancel(payment);
        return new ConfirmPaymentResponse(true, payment.getOrder().getId(), payment.getStatus().name());
    }

}
