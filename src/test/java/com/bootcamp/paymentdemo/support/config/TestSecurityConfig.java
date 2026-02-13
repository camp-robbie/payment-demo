package com.bootcamp.paymentdemo.support.config;

import com.bootcamp.paymentdemo.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtTokenProvider testJwtTokenProvider() {
        return new JwtTokenProvider(
            "test-secret-key-for-jwt-token-must-be-at-least-256-bits-long-for-hmac-sha",
            3600L
        );
    }
}
