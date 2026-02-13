package com.bootcamp.paymentdemo.support.fixture;

import com.bootcamp.paymentdemo.entity.UserEntity;

public class UserFixture {

    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_PASSWORD = "encodedPassword123";
    public static final String DEFAULT_NAME = "테스트유저";
    public static final String DEFAULT_PHONE = "01012345678";

    public static UserEntity aUser() {
        return UserEntity.register(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NAME, DEFAULT_PHONE);
    }

    public static UserEntity aUser(String email) {
        return UserEntity.register(email, DEFAULT_PASSWORD, DEFAULT_NAME, DEFAULT_PHONE);
    }

    public static UserEntity aUser(String email, String name) {
        return UserEntity.register(email, DEFAULT_PASSWORD, name, DEFAULT_PHONE);
    }
}
