package com.bootcamp.paymentdemo.e2e;

import com.bootcamp.paymentdemo.dto.ConfirmPaymentResponse;
import com.bootcamp.paymentdemo.dto.CreateOrderRequest;
import com.bootcamp.paymentdemo.dto.CreateOrderResponse;
import com.bootcamp.paymentdemo.dto.CreatePaymentRequest;
import com.bootcamp.paymentdemo.dto.CreatePaymentResponse;
import com.bootcamp.paymentdemo.dto.LoginRequest;
import com.bootcamp.paymentdemo.dto.LoginResponse;
import com.bootcamp.paymentdemo.dto.OrderItemRequest;
import com.bootcamp.paymentdemo.dto.ProductResponse;
import com.bootcamp.paymentdemo.dto.RegisterRequest;
import com.bootcamp.paymentdemo.dto.RegisterResponse;
import com.bootcamp.paymentdemo.support.base.BaseE2ETest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문-결제 E2E 테스트")
class OrderPaymentE2ETest extends BaseE2ETest {

    @Test
    @DisplayName("회원가입 → 로그인 → 상품 조회 → 주문 생성 → 결제 생성 → 결제 확인 전체 플로우")
    void 주문_결제_전체_플로우() {
        // 1. 회원가입
        var registerResponse = restClient.post()
            .uri("/api/auth/register")
            .body(new RegisterRequest("테스트유저", "e2e@test.com", "password123", "01099998888"))
            .retrieve()
            .toEntity(RegisterResponse.class);

        assertThat(registerResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(registerResponse.getBody().success()).isTrue();

        // 2. 로그인
        var loginResponse = restClient.post()
            .uri("/api/auth/login")
            .body(new LoginRequest("e2e@test.com", "password123"))
            .retrieve()
            .toEntity(LoginResponse.class);

        assertThat(loginResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(loginResponse.getBody().success()).isTrue();

        String token = loginResponse.getHeaders().getFirst("Authorization");
        assertThat(token).startsWith("Bearer ");

        // 인증된 클라이언트 생성
        RestClient authClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader("Authorization", token)
            .build();

        // 3. 상품 목록 조회 (DataSeeder가 5개 상품 시딩)
        ResponseEntity<List<ProductResponse>> productsResponse = authClient.get()
            .uri("/api/products")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<>() {});

        assertThat(productsResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(productsResponse.getBody()).hasSizeGreaterThanOrEqualTo(5);

        ProductResponse firstProduct = productsResponse.getBody().get(0);

        // 4. 주문 생성
        var orderResponse = authClient.post()
            .uri("/api/orders")
            .body(new CreateOrderRequest(List.of(new OrderItemRequest(firstProduct.id(), 1))))
            .retrieve()
            .toEntity(CreateOrderResponse.class);

        assertThat(orderResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(orderResponse.getBody().orderNumber()).startsWith("ORD-");
        assertThat(orderResponse.getBody().totalAmount()).isEqualTo(firstProduct.price());

        long orderId = orderResponse.getBody().orderId();
        long totalAmount = orderResponse.getBody().totalAmount();

        // 5. 결제 생성
        var paymentResponse = authClient.post()
            .uri("/api/payments")
            .body(new CreatePaymentRequest(orderId, totalAmount, 0L))
            .retrieve()
            .toEntity(CreatePaymentResponse.class);

        assertThat(paymentResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(paymentResponse.getBody().success()).isTrue();
        assertThat(paymentResponse.getBody().status()).isEqualTo("PENDING");

        long paymentId = paymentResponse.getBody().paymentId();

        // 6. 결제 확인
        var confirmResponse = authClient.post()
            .uri("/api/payments/{paymentId}/confirm", paymentId)
            .retrieve()
            .toEntity(ConfirmPaymentResponse.class);

        assertThat(confirmResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(confirmResponse.getBody().success()).isTrue();
        assertThat(confirmResponse.getBody().status()).isEqualTo("PAID");
    }
}
