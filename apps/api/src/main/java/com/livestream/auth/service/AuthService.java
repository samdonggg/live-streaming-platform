package com.livestream.auth.service;

import com.livestream.auth.config.TestAccountProperties;
import com.livestream.auth.domain.AuthUser;
import com.livestream.global.error.BusinessException;
import com.livestream.global.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * 로그인을 처리하고 인증 정보를 세션에 저장한다.
 * 로그인할 때마다 세션 ID와 CSRF 토큰을 새로 발급해 세션 고정 공격을 막는다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;
    private final LoginUserDetailsService userDetailsService;
    private final TestAccountProperties testAccountProperties;
    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    /**
     * 아이디와 비밀번호로 로그인한다.
     *
     * @throws BusinessException 아이디가 없거나 비밀번호가 틀리면 {@code INVALID_CREDENTIALS}
     */
    public AuthUser login(String loginId, String password, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(loginId, password));
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        return saveAuthentication(authentication, request, response);
    }

    /**
     * 비밀번호 없이 테스트 계정으로 로그인한다. 테스트 계정 설정이 켜져 있을 때만 동작한다.
     *
     * @throws BusinessException 설정이 꺼져 있거나 테스트 계정이 아니면 {@code TEST_ACCOUNT_NOT_FOUND}
     */
    public AuthUser loginAsTestAccount(String loginId, HttpServletRequest request, HttpServletResponse response) {
        if (!testAccountProperties.enabled() || !testAccountProperties.contains(loginId)) {
            throw new BusinessException(ErrorCode.TEST_ACCOUNT_NOT_FOUND);
        }
        AuthUser user = userDetailsService.loadUserByUsername(loginId);
        user.eraseCredentials();
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user, null, user.getAuthorities());
        return saveAuthentication(authentication, request, response);
    }

    /**
     * 바로 로그인할 수 있는 테스트 계정 목록을 돌려준다.
     *
     * @return 테스트 계정 설정이 꺼져 있으면 빈 목록
     */
    public List<TestAccountProperties.Account> testAccounts() {
        return testAccountProperties.enabled() ? testAccountProperties.accounts() : List.of();
    }

    private AuthUser saveAuthentication(
            Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        return (AuthUser) authentication.getPrincipal();
    }
}
