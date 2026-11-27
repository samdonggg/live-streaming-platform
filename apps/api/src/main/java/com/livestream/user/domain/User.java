package com.livestream.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 계정을 나타낸다.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false)
    private Instant createdAt;

    private User(String loginId, String password, String nickname, Instant createdAt) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    /**
     * 새 회원을 생성한다.
     *
     * @param encodedPassword 인코딩된 비밀번호 (평문 금지)
     * @param now             가입 일시
     */
    public static User create(String loginId, String encodedPassword, String nickname, Instant now) {
        return new User(loginId, encodedPassword, nickname, now);
    }
}
