package com.bootcamp.paymentdemo.controller;

import com.bootcamp.paymentdemo.support.base.BaseIntegrationTest;
import com.bootcamp.paymentdemo.support.helper.JwtTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ProductController 통합 테스트")
@AutoConfigureMockMvc
class ProductControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTestHelper jwtTestHelper;

    @Nested
    @DisplayName("GET /api/products")
    class ListProducts {

        @Test
        @DisplayName("인증된 사용자는 상품 목록을 조회할 수 있다")
        void 인증된_사용자는_상품_목록을_조회할_수_있다() throws Exception {
            mockMvc.perform(get("/api/products")
                    .header("Authorization", jwtTestHelper.bearerToken("test@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].price").isNumber())
                .andExpect(jsonPath("$[0].stock").isNumber());
        }

        @Test
        @DisplayName("인증하지 않으면 403을 반환한다")
        void 인증하지_않으면_403을_반환한다() throws Exception {
            mockMvc.perform(get("/api/products"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 403을 반환한다")
        void 유효하지_않은_토큰이면_403을_반환한다() throws Exception {
            mockMvc.perform(get("/api/products")
                    .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isForbidden());
        }
    }
}
