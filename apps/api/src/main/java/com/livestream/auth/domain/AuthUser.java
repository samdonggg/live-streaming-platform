package com.livestream.auth.domain;

import com.livestream.user.domain.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 세션에 저장되는 로그인 사용자 정보다.
 * 컨트롤러에서는 {@code @AuthenticationPrincipal AuthUser}로 받는다.
 * 인증이 끝나면 비밀번호를 지워 세션에 남지 않게 한다.
 */
@Getter
public class AuthUser implements UserDetails, CredentialsContainer {

    private final Long id;
    private final String loginId;
    private final String nickname;
    private String password;

    private AuthUser(Long id, String loginId, String nickname, String password) {
        this.id = id;
        this.loginId = loginId;
        this.nickname = nickname;
        this.password = password;
    }

    public static AuthUser from(User user) {
        return new AuthUser(user.getId(), user.getLoginId(), user.getNickname(), user.getPassword());
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
