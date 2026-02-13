package com.bootcamp.paymentdemo.domain;

import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.PaymentEntity;
import com.bootcamp.paymentdemo.entity.PaymentStatus;
import com.bootcamp.paymentdemo.entity.UserEntity;
import com.bootcamp.paymentdemo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentEntity createPayment(OrderEntity order, UserEntity user, long totalAmount, long pointsToUse) {
        order.ensurePayable(totalAmount);
        if (pointsToUse > 0) {
            user.ensureCanUsePoints(pointsToUse);
        }
        order.applyPoints(pointsToUse);
        return paymentRepository.save(PaymentEntity.create(order, user, totalAmount, pointsToUse));
    }

    @Transactional(readOnly = true)
    public PaymentEntity getForUser(String email, long paymentId) {
        return paymentRepository.findByIdAndUserEmail(paymentId, email)
            .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));
    }

    @Transactional
    public void markPaid(PaymentEntity payment) {
        payment.confirm();
    }

    @Transactional
    public void markCancelled(PaymentEntity payment) {
        payment.cancel();
    }

    @Transactional
    public void confirm(PaymentEntity payment) {
        if (isPaid(payment)) {
            return;
        }

        if (payment.getPointsToUse() > 0) {
            payment.getUser().consumePoints(payment.getPointsToUse());
        }
        payment.getOrder().markPaid();
        payment.confirm();
    }

    @Transactional
    public void cancel(PaymentEntity payment) {
        if (isCancelled(payment)) {
            return;
        }

        boolean wasPaid = isPaid(payment);
        payment.getOrder().markCancelled();
        payment.cancel();

        if (wasPaid && payment.getPointsToUse() > 0) {
            payment.getUser().refundPoints(payment.getPointsToUse());
        }
    }

    public boolean isPaid(PaymentEntity payment) {
        return payment.getStatus() == PaymentStatus.PAID;
    }

    public boolean isCancelled(PaymentEntity payment) {
        return payment.getStatus() == PaymentStatus.CANCELLED;
    }
}
