package com.bootcamp.paymentdemo.domain;

import com.bootcamp.paymentdemo.entity.OrderStatus;
import com.bootcamp.paymentdemo.entity.PaymentEntity;
import com.bootcamp.paymentdemo.entity.PaymentStatus;
import com.bootcamp.paymentdemo.repository.PaymentRepository;
import com.bootcamp.paymentdemo.support.base.BaseServiceTest;
import com.bootcamp.paymentdemo.support.fixture.OrderFixture;
import com.bootcamp.paymentdemo.support.fixture.PaymentFixture;
import com.bootcamp.paymentdemo.support.fixture.ProductFixture;
import com.bootcamp.paymentdemo.support.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class PaymentDomainServiceTest extends BaseServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentDomainService paymentDomainService;

    @Nested
    @DisplayName("createPayment")
    class CreatePayment {

        @Test
        @DisplayName("주문과 사용자 정보로 결제를 생성한다")
        void 주문과_사용자_정보로_결제를_생성한다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 2);

            given(paymentRepository.save(any(PaymentEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            // when
            PaymentEntity payment = paymentDomainService.createPayment(order, user, order.getTotalAmount(), 0L);

            // then
            assertThat(payment.getTotalAmount()).isEqualTo(order.getTotalAmount());
            assertThat(payment.getPointsToUse()).isZero();
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("포인트를 사용하여 결제를 생성한다")
        void 포인트를_사용하여_결제를_생성한다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct("상품", 500L, 10);
            var order = OrderFixture.anOrder(user, product, 1);
            long pointsToUse = 200L;

            given(paymentRepository.save(any(PaymentEntity.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            // when
            PaymentEntity payment = paymentDomainService.createPayment(order, user, order.getTotalAmount(), pointsToUse);

            // then
            assertThat(payment.getPointsToUse()).isEqualTo(200L);
            assertThat(order.getUsedPoints()).isEqualTo(200L);
        }

        @Test
        @DisplayName("PENDING 상태가 아닌 주문에 대해 결제 생성 시 예외가 발생한다")
        void PENDING_상태가_아닌_주문에_대해_결제_생성_시_예외가_발생한다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 1);
            order.markPaid();

            // when & then
            assertThatThrownBy(() ->
                paymentDomainService.createPayment(order, user, order.getTotalAmount(), 0L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("confirm")
    class Confirm {

        @Test
        @DisplayName("결제를 확인하면 결제와 주문이 PAID 상태가 된다")
        void 결제를_확인하면_결제와_주문이_PAID_상태가_된다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user);

            // when
            paymentDomainService.confirm(payment);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        }

        @Test
        @DisplayName("이미 결제 완료된 건은 무시한다")
        void 이미_결제_완료된_건은_무시한다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user);
            payment.confirm();

            // when
            paymentDomainService.confirm(payment);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
        }

        @Test
        @DisplayName("포인트 사용 결제 확인 시 사용자 포인트가 차감된다")
        void 포인트_사용_결제_확인_시_사용자_포인트가_차감된다() {
            // given
            var user = UserFixture.aUser(); // pointBalance = 1000
            var product = ProductFixture.aProduct("상품", 500L, 10);
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user, 300L);

            // when
            paymentDomainService.confirm(payment);

            // then
            assertThat(user.getPointBalance()).isEqualTo(700L);
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        @DisplayName("결제를 취소하면 결제와 주문이 CANCELLED 상태가 된다")
        void 결제를_취소하면_결제와_주문이_CANCELLED_상태가_된다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user);

            // when
            paymentDomainService.cancel(payment);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }

        @Test
        @DisplayName("이미 취소된 결제는 무시한다")
        void 이미_취소된_결제는_무시한다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user);
            payment.cancel();
            order.markCancelled();

            // when
            paymentDomainService.cancel(payment);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        }

        @Test
        @DisplayName("결제 완료 후 취소하면 포인트가 환불된다")
        void 결제_완료_후_취소하면_포인트가_환불된다() {
            // given
            var user = UserFixture.aUser(); // pointBalance = 1000
            var product = ProductFixture.aProduct("상품", 500L, 10);
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user, 300L);

            // 결제 확인 (포인트 차감: 1000 -> 700)
            paymentDomainService.confirm(payment);
            assertThat(user.getPointBalance()).isEqualTo(700L);

            // when (결제 취소 → 포인트 환불)
            paymentDomainService.cancel(payment);

            // then
            assertThat(user.getPointBalance()).isEqualTo(1000L);
        }
    }

    @Nested
    @DisplayName("getForUser")
    class GetForUser {

        @Test
        @DisplayName("사용자의 결제를 조회한다")
        void 사용자의_결제를_조회한다() {
            // given
            var user = UserFixture.aUser();
            var product = ProductFixture.aProduct();
            var order = OrderFixture.anOrder(user, product, 1);
            var payment = PaymentFixture.aPayment(order, user);

            given(paymentRepository.findByIdAndUserEmail(1L, "test@example.com"))
                .willReturn(Optional.of(payment));

            // when
            PaymentEntity result = paymentDomainService.getForUser("test@example.com", 1L);

            // then
            assertThat(result).isEqualTo(payment);
        }

        @Test
        @DisplayName("존재하지 않는 결제 조회 시 예외가 발생한다")
        void 존재하지_않는_결제_조회_시_예외가_발생한다() {
            // given
            given(paymentRepository.findByIdAndUserEmail(999L, "test@example.com"))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                paymentDomainService.getForUser("test@example.com", 999L))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
