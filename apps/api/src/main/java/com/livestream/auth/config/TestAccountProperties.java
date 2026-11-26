package com.livestream.auth.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 평가·시연용 테스트 계정 설정이다.
 * {@code enabled}가 켜져 있을 때만 서버 시작 시 계정을 만들고, 비밀번호 없이 바로 로그인할 수 있게 한다.
 *
 * @param password 모든 테스트 계정이 함께 쓰는 비밀번호. 일반 로그인 화면에서도 쓸 수 있다
 */
@ConfigurationProperties("app.test-accounts")
public record TestAccountProperties(boolean enabled, String password, List<Account> accounts) {

    public TestAccountProperties {
        accounts = accounts == null ? List.of() : List.copyOf(accounts);
    }

    public record Account(String loginId, String nickname) {
    }

    public boolean contains(String loginId) {
        return accounts.stream().anyMatch(account -> account.loginId().equals(loginId));
    }
}
