package com.livestream.global.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 현재 시각을 얻는 {@link Clock}을 등록한다.
 * 테스트에서 고정된 시계로 바꿀 수 있도록 {@code Instant.now()} 대신 이 빈을 주입받는다.
 */
@Configuration
public class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
