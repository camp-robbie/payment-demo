package com.bootcamp.paymentdemo.controller;

import com.bootcamp.paymentdemo.application.UserApplicationService;
import com.bootcamp.paymentdemo.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserApplicationService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(userService.getProfile(email));
    }
}
