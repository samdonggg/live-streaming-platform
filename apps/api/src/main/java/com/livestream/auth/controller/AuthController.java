package com.livestream.auth.controller;

import com.livestream.auth.domain.AuthUser;
import com.livestream.auth.dto.LoginRequest;
import com.livestream.auth.dto.SignupRequest;
import com.livestream.auth.dto.TestAccountResponse;
import com.livestream.auth.dto.TestLoginRequest;
import com.livestream.auth.service.AuthService;
import com.livestream.user.domain.User;
import com.livestream.user.dto.UserResponse;
import com.livestream.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원가입, 로그인, 현재 사용자 조회 API를 제공한다.
 * 로그아웃({@code POST /api/auth/logout})은 Spring Security 로그아웃 필터가 처리한다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        User user = userService.register(request.loginId(), request.password(), request.nickname());
        return new UserResponse(user.getId(), user.getLoginId(), user.getNickname());
    }

    @PostMapping("/login")
    public UserResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        AuthUser user = authService.login(request.loginId(), request.password(), httpRequest, httpResponse);
        return toResponse(user);
    }

    @PostMapping("/test-login")
    public UserResponse testLogin(
            @Valid @RequestBody TestLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        AuthUser user = authService.loginAsTestAccount(request.loginId(), httpRequest, httpResponse);
        return toResponse(user);
    }

    @GetMapping("/test-accounts")
    public List<TestAccountResponse> testAccounts() {
        return authService.testAccounts().stream()
                .map(account -> new TestAccountResponse(account.loginId(), account.nickname()))
                .toList();
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal AuthUser user) {
        return toResponse(user);
    }

    private UserResponse toResponse(AuthUser user) {
        return new UserResponse(user.getId(), user.getLoginId(), user.getNickname());
    }
}
