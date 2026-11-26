package com.livestream.auth.service;

import com.livestream.auth.config.TestAccountProperties;
import com.livestream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 테스트 계정 설정이 켜져 있으면 서버 시작 시 없는 테스트 계정을 만든다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestAccountInitializer implements ApplicationRunner {

    private final TestAccountProperties properties;
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) {
            return;
        }
        for (TestAccountProperties.Account account : properties.accounts()) {
            if (userService.findByLoginId(account.loginId()).isEmpty()) {
                userService.register(account.loginId(), properties.password(), account.nickname());
                log.info("테스트 계정 생성: {}", account.loginId());
            }
        }
    }
}
