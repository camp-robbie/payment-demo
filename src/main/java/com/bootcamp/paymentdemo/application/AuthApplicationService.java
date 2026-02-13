package com.bootcamp.paymentdemo.application;

import com.bootcamp.paymentdemo.domain.UserDomainService;
import com.bootcamp.paymentdemo.dto.RegisterRequest;
import com.bootcamp.paymentdemo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDomainService userDomainService;

    public String login(String email, String password) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        return jwtTokenProvider.createToken(email);
    }

    public void register(RegisterRequest request) {
        userDomainService.register(request);
    }
}
