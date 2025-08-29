package com.loopers.infrastructure.payment;

import com.loopers.support.config.ResiliencePolicyConfigurer;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import java.time.Duration;
import org.springframework.context.annotation.Configuration;
/**
 * 결제 게이트웨이 호출에 적용할 Resilience4j 정책 설정
 * - Retry 정책: 결제 요청이 실패했을 때 재시도 규칙 정의
 * - Circuit Breaker 정책: 특정 비율 이상 실패하면 회로를 열어 빠른 실패(Fail-Fast) 처리
 *
 * ResiliencePolicyConfigurer를 구현하여 ResiliencePolicyRegistrar가 실행될 때
 * RetryRegistry / CircuitBreakerRegistry에 정책을 등록한다.
 */
@Configuration
public class PaymentGatewayResilienceConfig implements ResiliencePolicyConfigurer {
    // 정책 이름
    public static final String RETRY_POLICY_NAME = "paymentRetry";
    public static final String CIRCUIT_BREAKER_POLICY_NAME = "paymentCircuitBreaker";

    // Retry 관련 상수
    private static final int RETRY_ATTEMPTS = 3;              // 최대 재시도 횟수
    private static final int RETRY_WAIT_DURATION_MILLIS = 500; // 초기 지연 시간 (ms)

    @Override
    public void configure(RetryRegistry retryRegistry, CircuitBreakerRegistry circuitBreakerRegistry) {
        // 결제 모듈용 Retry 정책 등록
        retryRegistry.retry(RETRY_POLICY_NAME, retryConfig());
        // 결제 모듈용 Circuit Breaker 정책 등록
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_POLICY_NAME, circuitBreakerConfig());
    }

    /**
     * Retry 정책 정의
     * - 최대 3번까지 재시도
     * - 지수적 backoff + 랜덤 지터를 적용
     * - 예외에 따라 무시/재시도 여부를 분리
     */
    private RetryConfig retryConfig() {
        // IntervalFunction: 지수 백오프 + 랜덤
        // 지수적 백오프 (Exponential Backoff): 재시도가 반복될수록 대기 시간이 기하급수적으로 늘어남.
        // 랜덤 지터 (Random Jitter): 네트워크에 몰리지 않도록 대기 시간을 약간 랜덤하게 섞어줌.
        IntervalFunction intervalFunction =
            IntervalFunction.ofExponentialRandomBackoff(
                Duration.ofMillis(RETRY_WAIT_DURATION_MILLIS), // 초기 지연 시간
                2.0,       // 지수 승수 (예: 2배씩 증가)
                0.3        // 지터 비율 (±30% 랜덤)
            );

        return RetryConfig.custom()
            .maxAttempts(RETRY_ATTEMPTS)        // 최대 재시도 횟수
            .intervalFunction(intervalFunction) // 지수 백오프 전략 적용
            //  예외 분류
            .ignoreExceptions(
                // 400: 잘못된 요청 → 재시도해도 성공할 수 없음
                feign.FeignException.BadRequest.class,
                // 401: 인증 오류 → 재시도해도 해결 불가
                feign.FeignException.Unauthorized.class
            )
            .retryExceptions(
                // 500: PG 서버 내부 오류 → 재시도로 회복 가능성 있음
                FeignException.InternalServerError.class,
                // 네트워크 문제 등 → 재시도 대상
                feign.RetryableException.class
            )
            .build();
    }

    /**
     * Circuit Breaker 정책 정의
     * - 최근 호출들의 성공/실패를 기반으로 회로를 열거나 닫음
     * - 빠른 실패(Fail-Fast) 전략으로 불필요한 외부 호출 방지
     */
    private CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            // 슬라이딩 윈도우 방식: 시간 기반
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
            .slidingWindowSize(10) // 최근 10초 동안의 호출 통계를 기반으로 판단
            .minimumNumberOfCalls(4) // 최소 4번 호출 이후부터 통계 적용
            .failureRateThreshold(50f) // 실패율 50% 이상이면 Open 상태로 전환
            .slowCallRateThreshold(50f) // 느린 호출 비율 50% 이상이면 Open
            .slowCallDurationThreshold(Duration.ofMillis(100)) // 100ms 이상이면 "느린 호출"로 간주
            .waitDurationInOpenState(Duration.ofMillis(2000)) // Open → Half-Open으로 전환되기까지 대기 시간 (2초)
            .automaticTransitionFromOpenToHalfOpenEnabled(true) // 자동으로 Half-Open으로 전환
            .permittedNumberOfCallsInHalfOpenState(2) // Half-Open 상태에서 허용되는 호출 수
            .maxWaitDurationInHalfOpenState(Duration.ofMillis(3000)) // Half-Open 유지 최대 시간 (3초)
            // 예외 분류
            .recordExceptions(
                feign.RetryableException.class,             // 네트워크 예외 → 실패로 기록
                feign.FeignException.InternalServerError.class // 서버 오류 → 실패로 기록
            )
            .ignoreExceptions(
                feign.FeignException.BadRequest.class,      // 잘못된 요청(400) → 실패로 보지 않음
                feign.FeignException.Unauthorized.class     // 인증 실패(401) → 실패로 보지 않음
            )
            .build();
    }

}
