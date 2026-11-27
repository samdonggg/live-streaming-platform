package com.livestream.auth.service;

import com.livestream.auth.domain.AuthUser;
import com.livestream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 로그인 아이디로 회원을 찾아 Spring Security가 비밀번호를 검증할 수 있게 넘긴다.
 */
@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public AuthUser loadUserByUsername(String loginId) {
        return userService.findByLoginId(loginId)
                .map(AuthUser::from)
                .orElseThrow(() -> new UsernameNotFoundException(loginId));
    }
}
