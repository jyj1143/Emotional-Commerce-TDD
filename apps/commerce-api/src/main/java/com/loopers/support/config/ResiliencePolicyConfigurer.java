package com.loopers.support.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;

/**
 * Resilience4j의 Retry, CircuitBreaker 정책을 등록할 수 있는 인터페이스
 * - 모듈별로 이 인터페이스를 구현하여 자신만의 정책을 정의
 * - Registrar가 실행되면 모든 구현체의 configure 메서드를 호출하여 정책을 등록함
 */
public interface ResiliencePolicyConfigurer {
    /**
     * RetryRegistry, CircuitBreakerRegistry에 정책을 등록하는 메서드
     * @param retry Retry 정책 저장소
     * @param cb CircuitBreaker 정책 저장소
     */
    void configure(RetryRegistry retry, CircuitBreakerRegistry cb);
}
