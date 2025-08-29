package com.loopers.support.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;

/**
 * Resilience4j 정책을 실제로 등록하는 Registrar
 * - Spring이 모든 Bean을 초기화한 이후 실행됨
 * - 모든 ResiliencePolicyConfigurer 구현체를 순회하며 정책을 등록함
 */
@Configuration
@RequiredArgsConstructor
public class ResiliencePolicyRegistrar implements SmartInitializingSingleton {
    // Resilience4j의 Retry 정책 저장소
    private final RetryRegistry retryRegistry;

    // Resilience4j의 CircuitBreaker 정책 저장소
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    // Spring이 주입하는 모든 ResiliencePolicyConfigurer 구현체들
    private final List<ResiliencePolicyConfigurer> configurers;

    /**
     * 모든 Singleton Bean이 초기화된 후 실행되는 콜백 메서드
     * - 여기서 각 Configurer의 configure 메서드를 호출하여 정책을 등록함
     */
    @Override
    public void afterSingletonsInstantiated() {
        for (var configure : configurers){
            // 모듈별 Configurer가 자신만의 Retry, CircuitBreaker 정책을 레지스트리에 등록
            configure.configure(retryRegistry, circuitBreakerRegistry);
        }
    }
}
