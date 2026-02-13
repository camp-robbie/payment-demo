package com.bootcamp.paymentdemo.application;

import com.bootcamp.paymentdemo.domain.UserDomainService;
import com.bootcamp.paymentdemo.dto.UserProfileResponse;
import com.bootcamp.paymentdemo.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserDomainService userDomainService;

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        UserEntity user = userDomainService.getByEmail(email);
        return new UserProfileResponse(
            user.getCustomerUid(),
            user.getEmail(),
            user.getName(),
            user.getPhone(),
            user.getPointBalance()
        );
    }
}
