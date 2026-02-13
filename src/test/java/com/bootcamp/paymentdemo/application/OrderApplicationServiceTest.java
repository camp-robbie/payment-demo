package com.bootcamp.paymentdemo.application;

import com.bootcamp.paymentdemo.domain.OrderDomainService;
import com.bootcamp.paymentdemo.domain.ProductDomainService;
import com.bootcamp.paymentdemo.domain.UserDomainService;
import com.bootcamp.paymentdemo.dto.CreateOrderRequest;
import com.bootcamp.paymentdemo.dto.CreateOrderResponse;
import com.bootcamp.paymentdemo.dto.OrderItemRequest;
import com.bootcamp.paymentdemo.entity.OrderEntity;
import com.bootcamp.paymentdemo.entity.ProductEntity;
import com.bootcamp.paymentdemo.entity.UserEntity;
import com.bootcamp.paymentdemo.support.base.BaseServiceTest;
import com.bootcamp.paymentdemo.support.fixture.ProductFixture;
import com.bootcamp.paymentdemo.support.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class OrderApplicationServiceTest extends BaseServiceTest {

    @Mock
    private OrderDomainService orderDomainService;

    @Mock
    private ProductDomainService productDomainService;

    @Mock
    private UserDomainService userDomainService;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    @Nested
    @DisplayName("createOrder")
    class CreateOrder {

        @Test
        @DisplayName("주문을 성공적으로 생성한다")
        void 주문을_성공적으로_생성한다() {
            // given
            String email = "test@example.com";
            UserEntity user = UserFixture.aUser(email);
            ProductEntity product = ProductFixture.aProduct("테스트 상품", 10000L, 50);

            var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(1L, 2)
            ));

            OrderEntity order = OrderEntity.create(user);
            order.addItem(product, 2);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(userDomainService.getByEmail(email)).willReturn(user);
            given(productDomainService.getById(1L)).willReturn(product);
            given(orderDomainService.create(eq(user), any())).willReturn(order);

            // when
            CreateOrderResponse response = orderApplicationService.createOrder(email, request);

            // then
            assertThat(response.orderId()).isEqualTo(1L);
            assertThat(response.totalAmount()).isEqualTo(20000L);
            assertThat(response.orderNumber()).startsWith("ORD-");
        }

        @Test
        @DisplayName("상품 ID가 null이면 예외가 발생한다")
        void 상품_ID가_null이면_예외가_발생한다() {
            // given
            String email = "test@example.com";
            UserEntity user = UserFixture.aUser(email);

            var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(null, 2)
            ));

            given(userDomainService.getByEmail(email)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> orderApplicationService.createOrder(email, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 ID와 수량은 필수입니다.");
        }

        @Test
        @DisplayName("수량이 null이면 예외가 발생한다")
        void 수량이_null이면_예외가_발생한다() {
            // given
            String email = "test@example.com";
            UserEntity user = UserFixture.aUser(email);

            var request = new CreateOrderRequest(List.of(
                new OrderItemRequest(1L, null)
            ));

            given(userDomainService.getByEmail(email)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> orderApplicationService.createOrder(email, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 ID와 수량은 필수입니다.");
        }
    }

    @Nested
    @DisplayName("listOrders")
    class ListOrders {

        @Test
        @DisplayName("사용자의 주문 목록을 조회한다")
        void 사용자의_주문_목록을_조회한다() {
            // given
            String email = "test@example.com";
            UserEntity user = UserFixture.aUser(email);
            ProductEntity product = ProductFixture.aProduct();
            OrderEntity order = OrderEntity.create(user);
            order.addItem(product, 1);
            ReflectionTestUtils.setField(order, "id", 1L);

            given(orderDomainService.listForUser(email)).willReturn(List.of(order));

            // when
            var result = orderApplicationService.listOrders(email);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).orderNumber()).startsWith("ORD-");
            assertThat(result.get(0).status()).isEqualTo("PENDING");
        }
    }
}
