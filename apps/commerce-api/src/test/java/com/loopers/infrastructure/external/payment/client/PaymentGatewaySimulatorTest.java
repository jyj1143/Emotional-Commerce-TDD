package com.loopers.infrastructure.external.payment.client;

import com.loopers.domain.payment.adapter.PaymentGatewayCommand.Payment;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Order;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Transaction;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.TransactionDetail;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.infrastructure.external.payment.dto.PgClientV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles("test")
class PaymentGatewaySimulatorTest {

    @Autowired
    private PaymentGatewaySimulator paymentGatewaySimulator;

    @MockitoBean
    private PgV1Client pgV1Client;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;
    private Payment testPaymentCommand;
    private String testTransactionKey;
    private String testOrderId;

    @BeforeEach
    void setUp() {
        // 실제 설정에 맞춘 서킷브레이커 이름 사용
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("pgClient");
        circuitBreaker.reset(); // 각 테스트 전 상태 초기화

        // 테스트 데이터 설정
        testPaymentCommand = new Payment(1L, CardType.SAMSUNG, "1234-5678-1234-5678", 10000L);
        testTransactionKey = "TXN_TEST_123";
        testOrderId = "ORDER_123";
    }

    @Test
    @DisplayName("서킷브레이커 - 최소 10회 호출 후 50% 실패시 OPEN 상태로 변경")
    void processPayment_CircuitBreakerOpensAfter10Calls() {
        // Given - 실패 응답 설정
        given(pgV1Client.processPayment(any(), any()))
                .willThrow(new RuntimeException("PG 서비스 장애"));

        // When - 10회 호출하여 모두 실패시킴 (100% 실패율로 50% 임계값 초과)
        for (int i = 0; i < 10; i++) {
            assertThatThrownBy(() -> paymentGatewaySimulator.processPayment(testPaymentCommand))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("결제 서비스에 일시적인 문제가 발생했습니다");
        }

        // Then - 서킷브레이커가 OPEN 상태로 변경되어야 함
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("슬로우콜 - 1초 초과 응답이 50% 이상이면 서킷브레이커 OPEN")
    void processPayment_SlowCallCircuitBreaker() {
        // Given - 느린 응답 시뮬레이션
        PgClientV1Dto.TransactionResponse slowResponse = new PgClientV1Dto.TransactionResponse(
                testTransactionKey, PgClientV1Dto.TransactionStatusDto.SUCCESS, "느린 결제 성공"
        );
        PaymentClientApiResponse.Metadata successMeta = new PaymentClientApiResponse.Metadata(
                PaymentClientApiResponse.Metadata.Result.SUCCESS, null, "성공"
        );
        PaymentClientApiResponse<PgClientV1Dto.TransactionResponse> apiResponse =
                new PaymentClientApiResponse<>(successMeta, slowResponse);

        given(pgV1Client.processPayment(any(), any()))
                .willAnswer(invocation -> {
                    Thread.sleep(1500); // 1.5초 대기 (1초 임계값 초과)
                    return apiResponse;
                });

        // When - 10회 호출하여 모두 슬로우콜로 만듦
        for (int i = 0; i < 10; i++) {
            try {
                paymentGatewaySimulator.processPayment(testPaymentCommand);
            } catch (Exception e) {
                // 타임아웃이나 서킷브레이커로 인한 예외 발생 가능
            }
        }

        // Then - 서킷브레이커가 OPEN 상태로 변경되어야 함
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("재시도 - 첫 번째 실패 후 재시도하여 성공")
    void processPayment_RetrySuccess() {
        // Given - 첫 번째 호출은 실패, 두 번째는 성공
        PgClientV1Dto.TransactionResponse successResponse = new PgClientV1Dto.TransactionResponse(
                testTransactionKey, PgClientV1Dto.TransactionStatusDto.SUCCESS, "재시도 성공"
        );
        PaymentClientApiResponse.Metadata successMeta = new PaymentClientApiResponse.Metadata(
                PaymentClientApiResponse.Metadata.Result.SUCCESS, null, "성공"
        );
        PaymentClientApiResponse<PgClientV1Dto.TransactionResponse> apiResponse =
                new PaymentClientApiResponse<>(successMeta, successResponse);

        given(pgV1Client.processPayment(any(), any()))
                .willThrow(new RuntimeException("일시적 오류"))
                .willReturn(apiResponse);

        // When
        Transaction result = paymentGatewaySimulator.processPayment(testPaymentCommand);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.transactionKey()).isEqualTo(testTransactionKey);
        assertThat(result.reason()).isEqualTo("재시도 성공");
    }

    @Test
    @DisplayName("결제 처리 폴백 - 4xx 에러시 BAD_REQUEST 예외 발생")
    void processPaymentFallback() {
        // Given
        PaymentClientException clientException = new PaymentClientException(
                HttpStatus.BAD_REQUEST, "잘못된 카드 정보"
        );

        // When & Then
        assertThatThrownBy(() ->
                paymentGatewaySimulator.processPaymentFallback(testPaymentCommand, clientException))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.BAD_REQUEST);
    }

    @Test
    @DisplayName("트랜잭션 조회 폴백 - 서버 오류시 INTERNAL_ERROR 예외 발생")
    void getTransactionFallback() {
        // Given
        RuntimeException serverException = new RuntimeException("서버 내부 오류");

        // When & Then
        assertThatThrownBy(() ->
                paymentGatewaySimulator.getTransactionFallback(testTransactionKey, serverException))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INTERNAL_ERROR);
    }

    @Test
    @DisplayName("주문별 결제 조회 폴백 - 클라이언트 오류시 BAD_REQUEST 예외 발생")
    void getPaymentsByOrderIdFallback() {
        // Given
        PaymentClientException clientException = new PaymentClientException(
                HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다"
        );

        // When & Then
        assertThatThrownBy(() ->
                paymentGatewaySimulator.getPaymentsByOrderIdFallback(testOrderId, clientException))
                .isInstanceOf(CoreException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.BAD_REQUEST);
    }

    @Test
    @DisplayName("동시 요청시 서킷브레이커가 올바르게 동작한다")
    void processPayment_ConcurrentCalls() throws InterruptedException {
        // Given
        given(pgV1Client.processPayment(any(), any()))
                .willThrow(new RuntimeException("PG 서비스 장애"));

        ExecutorService executor = Executors.newFixedThreadPool(15);
        CountDownLatch latch = new CountDownLatch(15);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When - 동시에 15개 요청 실행
        for (int i = 0; i < 15; i++) {
            executor.submit(() -> {
                try {
                    paymentGatewaySimulator.processPayment(testPaymentCommand);
                } catch (CoreException e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        // Then
        assertThat(failureCount.get()).isEqualTo(15);
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        executor.shutdown();
    }

    @Test
    @DisplayName("서킷브레이커 메트릭 확인")
    void processPayment_CircuitBreakerMetrics() {
        // Given
        PgClientV1Dto.TransactionResponse successResponse = new PgClientV1Dto.TransactionResponse(
                testTransactionKey, PgClientV1Dto.TransactionStatusDto.SUCCESS, "성공"
        );
        PaymentClientApiResponse.Metadata successMeta = new PaymentClientApiResponse.Metadata(
                PaymentClientApiResponse.Metadata.Result.SUCCESS, null, "성공"
        );
        given(pgV1Client.processPayment(any(), any()))
                .willReturn(new PaymentClientApiResponse<>(successMeta, successResponse));

        // When - 성공 호출
        paymentGatewaySimulator.processPayment(testPaymentCommand);

        // Then - 메트릭 확인
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfSuccessfulCalls()).isGreaterThanOrEqualTo(1);
        assertThat(metrics.getFailureRate()).isLessThan(50.0f);
    }

    @Test
    @DisplayName("PG 응답 실패시 메타데이터 FAIL로 처리")
    void processPayment_FailMetadata() {
        // Given
        PgClientV1Dto.TransactionResponse failResponse = new PgClientV1Dto.TransactionResponse(
                testTransactionKey, PgClientV1Dto.TransactionStatusDto.FAILED, "결제 실패"
        );
        PaymentClientApiResponse.Metadata failMeta = new PaymentClientApiResponse.Metadata(
                PaymentClientApiResponse.Metadata.Result.FAIL, "PAYMENT_FAIL", "결제 처리 실패"
        );
        PaymentClientApiResponse<PgClientV1Dto.TransactionResponse> apiResponse =
                new PaymentClientApiResponse<>(failMeta, failResponse);

        given(pgV1Client.processPayment(any(), any()))
                .willReturn(apiResponse);

        // When
        Transaction result = paymentGatewaySimulator.processPayment(testPaymentCommand);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.transactionKey()).isEqualTo(testTransactionKey);
        assertThat(result.reason()).isEqualTo("결제 실패");
    }

    @Test
    @DisplayName("혼합 상황 - 실패와 슬로우콜이 섞인 경우 서킷브레이커 OPEN")
    void processPayment_MixedFailureAndSlowCalls() {
        // Given - 5회 실패, 5회 슬로우콜 설정
        PaymentClientApiResponse.Metadata successMeta = new PaymentClientApiResponse.Metadata(
                PaymentClientApiResponse.Metadata.Result.SUCCESS, null, "느린 성공"
        );

        given(pgV1Client.processPayment(any(), any()))
                .willThrow(new RuntimeException("실패"))
                .willThrow(new RuntimeException("실패"))
                .willThrow(new RuntimeException("실패"))
                .willThrow(new RuntimeException("실패"))
                .willThrow(new RuntimeException("실패"))
                .willAnswer(invocation -> {
                    Thread.sleep(1500); // 슬로우콜
                    return new PaymentClientApiResponse<>(
                            successMeta,
                            new PgClientV1Dto.TransactionResponse("SLOW_1", PgClientV1Dto.TransactionStatusDto.SUCCESS, "느린 성공")
                    );
                })
                .willAnswer(invocation -> {
                    Thread.sleep(1500);
                    return new PaymentClientApiResponse<>(
                            successMeta,
                            new PgClientV1Dto.TransactionResponse("SLOW_2", PgClientV1Dto.TransactionStatusDto.SUCCESS, "느린 성공")
                    );
                })
                .willAnswer(invocation -> {
                    Thread.sleep(1500);
                    return new PaymentClientApiResponse<>(
                            successMeta,
                            new PgClientV1Dto.TransactionResponse("SLOW_3", PgClientV1Dto.TransactionStatusDto.SUCCESS, "느린 성공")
                    );
                })
                .willAnswer(invocation -> {
                    Thread.sleep(1500);
                    return new PaymentClientApiResponse<>(
                            successMeta,
                            new PgClientV1Dto.TransactionResponse("SLOW_4", PgClientV1Dto.TransactionStatusDto.SUCCESS, "느린 성공")
                    );
                })
                .willAnswer(invocation -> {
                    Thread.sleep(1500);
                    return new PaymentClientApiResponse<>(
                            successMeta,
                            new PgClientV1Dto.TransactionResponse("SLOW_5", PgClientV1Dto.TransactionStatusDto.SUCCESS, "느린 성공")
                    );
                });

        // When - 10회 호출
        for (int i = 0; i < 10; i++) {
            try {
                paymentGatewaySimulator.processPayment(testPaymentCommand);
            } catch (Exception ignored) {
                // 실패나 타임아웃으로 예외 발생 가능
            }
        }

        // Then - 실패율 50% + 슬로우콜율 50%로 서킷브레이커 OPEN
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
