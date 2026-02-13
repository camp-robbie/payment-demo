package com.bootcamp.paymentdemo.controller;

import com.bootcamp.paymentdemo.application.AuthApplicationService;
import com.bootcamp.paymentdemo.dto.LoginRequest;
import com.bootcamp.paymentdemo.dto.LoginResponse;
import com.bootcamp.paymentdemo.dto.RegisterRequest;
import com.bootcamp.paymentdemo.dto.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 * 구현할 API 엔드포인트 템플릿
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authService;

    /**
     * 회원가입 API
     * POST /api/auth/register
     *
     * 요청 본문:
     * {
     *   "name": "홍길동",
     *   "email": "user@example.com",
     *   "password": "password123",
     *   "phone": "01012345678"
     * }
     *
     * 응답 본문:
     * {
     *   "success": true,
     *   "message": "회원가입 완료"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok(new RegisterResponse(true, "회원가입 완료"));
        } catch (IllegalArgumentException e) {
            if ("이미 가입된 이메일입니다.".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponse(false, e.getMessage()));
            }
            return ResponseEntity.badRequest()
                .body(new RegisterResponse(false, e.getMessage()));
        }
    }

    /**
     * 로그인 API
     * POST /api/auth/login
     *
     * 요청 본문:
     * {
     *   "email": "user@example.com",
     *   "password": "password123"
     * }
     *
     * 응답 헤더:
     * Authorization: Bearer eyJhbGc...
     *
     * 응답 본문:
     * {
     *   "success": true,
     *   "email": "user@example.com"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String email = request.email();
        String password = request.password();
        try {
            String token = authService.login(email, password);

            return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .body(new LoginResponse(true, email, null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, null, "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }
}
