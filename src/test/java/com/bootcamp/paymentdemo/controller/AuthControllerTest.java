package com.bootcamp.paymentdemo.controller;

import com.bootcamp.paymentdemo.application.AuthApplicationService;
import com.bootcamp.paymentdemo.dto.RegisterRequest;
import com.bootcamp.paymentdemo.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthApplicationService authApplicationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("회원가입 성공 시 200과 success=true를 반환한다")
        void 회원가입_성공() throws Exception {
            // given
            willDoNothing().given(authApplicationService).register(any(RegisterRequest.class));

            // when & then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "홍길동",
                            "email": "test@example.com",
                            "password": "password123",
                            "phone": "01012345678"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입 완료"));
        }

        @Test
        @DisplayName("이미 가입된 이메일이면 409 Conflict를 반환한다")
        void 이미_가입된_이메일이면_409() throws Exception {
            // given
            willThrow(new IllegalArgumentException("이미 가입된 이메일입니다."))
                .given(authApplicationService).register(any(RegisterRequest.class));

            // when & then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "홍길동",
                            "email": "dup@example.com",
                            "password": "password123",
                            "phone": "01012345678"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
        }

        @Test
        @DisplayName("필수 항목 누락 시 400 Bad Request를 반환한다")
        void 필수_항목_누락시_400() throws Exception {
            // given
            willThrow(new IllegalArgumentException("필수 항목이 누락되었습니다."))
                .given(authApplicationService).register(any(RegisterRequest.class));

            // when & then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "홍길동",
                            "email": null,
                            "password": null,
                            "phone": "01012345678"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("필수 항목이 누락되었습니다."));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("로그인 성공 시 200과 Authorization 헤더를 반환한다")
        void 로그인_성공() throws Exception {
            // given
            given(authApplicationService.login("test@example.com", "password123"))
                .willReturn("jwt-test-token");

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "email": "test@example.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer jwt-test-token"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("로그인 실패 시 401을 반환한다")
        void 로그인_실패() throws Exception {
            // given
            given(authApplicationService.login("wrong@example.com", "wrongpass"))
                .willThrow(new RuntimeException("Bad credentials"));

            // when & then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "email": "wrong@example.com",
                            "password": "wrongpass"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }
}
