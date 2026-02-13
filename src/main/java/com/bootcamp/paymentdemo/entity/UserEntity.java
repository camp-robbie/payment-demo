package com.bootcamp.paymentdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(nullable = false, length = 200)
    private String customerUid;

    @Column(nullable = false)
    private long pointBalance;

    @Column(nullable = false, length = 30)
    private String role;

    public static UserEntity register(String email, String encodedPassword, String name, String phone) {
        UserEntity user = new UserEntity();
        user.email = email;
        user.password = encodedPassword;
        user.name = name;
        user.phone = phone;
        user.customerUid = "CUST_" + Math.abs(email.hashCode());
        user.pointBalance = 1000L;
        user.role = "USER";
        return user;
    }

    public void consumePoints(long points) {
        if (points < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
        ensureCanUsePoints(points);
        pointBalance -= points;
    }

    public void refundPoints(long points) {
        if (points < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
        pointBalance += points;
    }

    public void ensureCanUsePoints(long points) {
        if (points < 0) {
            throw new IllegalArgumentException("포인트는 0 이상이어야 합니다.");
        }
        if (pointBalance < points) {
            throw new IllegalArgumentException("포인트 잔액이 부족합니다.");
        }
    }
}
