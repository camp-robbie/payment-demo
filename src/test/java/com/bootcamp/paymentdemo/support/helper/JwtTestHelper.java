package com.bootcamp.paymentdemo.support.helper;

import com.bootcamp.paymentdemo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTestHelper {

    private final JwtTokenProvider jwtTokenProvider;

    public String createToken(String email) {
        return jwtTokenProvider.createToken(email);
    }

    public String bearerToken(String email) {
        return "Bearer " + createToken(email);
    }
}
