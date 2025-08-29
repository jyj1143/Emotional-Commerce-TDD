
package com.loopers.infrastructure.external.payment.client;

import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayCommand.Payment;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Order;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Transaction;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.TransactionDetail;
import com.loopers.infrastructure.external.payment.dto.PgClientV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGatewaySimulator implements PaymentGatewayAdapter {

    private final PgV1Client pgV1Client;
    @Value("${pg-simulator.callback-url}")
    private String callbackUrl;
    @Value("${pg-simulator.store-id}")
    private String storeId;

    @Retry(name = "paymentRetry", fallbackMethod = "processPaymentFallback")
    @CircuitBreaker(name = "pgClient", fallbackMethod = "processPaymentFallback")
    @Override
    public Transaction processPayment(Payment paymentCommand) {
        PgClientV1Dto.PaymentRequest paymentRequest =  new PgClientV1Dto.PaymentRequest(
            paymentCommand.orderId(),
            paymentCommand.cardType().name(),
            paymentCommand.cardNo(),
            paymentCommand.amount(),
            callbackUrl
        );

        // PG 클라이언트 호출 및 응답 처리
        PaymentClientApiResponse<PgClientV1Dto.TransactionResponse> pgResponse =
            pgV1Client.processPayment(storeId, paymentRequest);

        return pgResponse.data().toTransaction();
    }

    /**
     * PaymentClientException 발생 시 폴백 처리
     */
    public Transaction processPaymentFallback(Payment paymentCommand, PaymentClientException exception) {
        // HTTP 상태 코드에 따른 메시지 결정
        String errorMessage;
        int statusCode = exception.getHttpStatus().value();

        if (isClientError(statusCode)) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = "결제 서버와의 통신 중 일시적인 오류가 발생했습니다. 잠시 후에 다시 시도해 주시기 바랍니다.";
        }

        return createFailedTransaction(exception, errorMessage);
    }

    /**
     * 일반 Exception 발생 시 폴백 처리
     */
    public Transaction processPaymentFallback(Payment paymentCommand, Exception exception) {
        String errorMessage = "결제 서비스 연결 중 기술적인 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.";
        return createFailedTransaction(exception, errorMessage);
    }

    /**
     * 실패한 트랜잭션 객체 생성 및 로깅
     */
    private Transaction createFailedTransaction(Exception exception, String errorMessage) {
        log.error("결제 처리 폴백 실행 - 원인: {}", exception.getMessage());

        if (exception instanceof PaymentClientException clientException) {
            log.error("PG 시뮬레이터 응답 오류 - 상태코드: {}, 에러타입: {}, 상세내용: {}",
                clientException.getHttpStatus(), clientException.getErrorType(), clientException.getMessage());
        } else {
            log.error("결제 처리 중 예외 발생 - 예외 유형: {}", exception.getClass().getSimpleName(), exception);
        }

        return new Transaction(null, null, errorMessage, false);
    }

    /**
     * 클라이언트 에러인지 확인 (400번대)
     */
    private boolean isClientError(int statusCode) {
        return statusCode >= 400 && statusCode < 500;
    }

    @Retry(name = "transactionRetry", fallbackMethod = "getTransactionFallback")
    @CircuitBreaker(name = "pgClient", fallbackMethod = "getTransactionFallback")
    @Override
    public TransactionDetail getTransaction(String transactionKey) {
        PaymentClientApiResponse<PgClientV1Dto.TransactionDetailResponse> response = pgV1Client.getTransaction(storeId, transactionKey);
        return response.data().toTransactionDetail();
    }

    public TransactionDetail getTransactionFallback(String transactionKey, Throwable t) {
        log.error("결제 정보 조회 실패 - 폴백 메소드 실행: {}", t.getMessage());
        if (t instanceof PaymentClientException e) {
            if (e.getHttpStatus().is4xxClientError()) {
                throw new CoreException(ErrorType.BAD_REQUEST,
                    "결제 정보 요청이 올바르지 않습니다: " + e.getCustomMessage());
            }
        }
        throw new CoreException(ErrorType.INTERNAL_ERROR,
            "결제 정보 조회 중 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }

    @Retry(name = "orderRetry", fallbackMethod = "getPaymentsByOrderIdFallback")
    @CircuitBreaker(name = "pgClient", fallbackMethod = "getPaymentsByOrderIdFallback")
    @Override
    public Order getPaymentsByOrderId(String orderId) {
        PaymentClientApiResponse<PgClientV1Dto.OrderResponse> response = pgV1Client.getPaymentsByOrderId(storeId, orderId);
        PgClientV1Dto.OrderResponse orderResponse = response.data();
        return orderResponse.toOrder();
    }

    public Order getPaymentsByOrderIdFallback(String orderId, Throwable t) {
        log.error("주문에 엮인 결제 정보 조회 실패 - 폴백 메소드 실행: {}", t.getMessage());
        if (t instanceof PaymentClientException e) {
            if (e.getHttpStatus().is4xxClientError()) {
                throw new CoreException(ErrorType.BAD_REQUEST,
                    "주문 정보 요청이 올바르지 않습니다: " + e.getCustomMessage());
            }
        }
        throw new CoreException(ErrorType.INTERNAL_ERROR,
            "주문에 엮인 결제 정보 조회 중 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
