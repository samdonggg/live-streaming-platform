package com.livestream.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TestLoginRequest(@NotBlank(message = "테스트 계정 아이디를 입력해 주세요.") String loginId) {
}
