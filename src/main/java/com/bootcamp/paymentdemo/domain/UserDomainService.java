package com.bootcamp.paymentdemo.domain;

import com.bootcamp.paymentdemo.dto.RegisterRequest;
import com.bootcamp.paymentdemo.entity.UserEntity;
import com.bootcamp.paymentdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity register(RegisterRequest request) {
        if (request.email() == null || request.password() == null || request.name() == null || request.phone() == null) {
            throw new IllegalArgumentException("필수 항목이 누락되었습니다.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        UserEntity user = UserEntity.register(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.name(),
            request.phone()
        );

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserEntity getByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
