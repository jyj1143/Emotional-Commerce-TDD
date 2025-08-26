package com.loopers.support.config;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenFeign Config
 */
@Configuration
public class FeignConfiguration {
    /**
     * Feign 로깅 레벨 설정
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;  // 모니터링을 위한 상세 로깅
    }
    /**
     * Feign 클라이언트의 재시도 비활성화
     * Resilience4j로 재시도 로직을 대체하기 위해 Feign 자체 재시도 기능은 비활성화합니다.
     */
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
}
