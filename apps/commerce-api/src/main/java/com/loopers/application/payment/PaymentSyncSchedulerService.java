package com.loopers.application.payment;

import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.TransactionDetail;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.entity.PaymentModel;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.repository.PaymentRepository;
import com.loopers.domain.payment.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSyncSchedulerService {

    private static final int SYNC_INTERVAL_SECONDS = 120;
    private static final int INITIAL_DELAY_SECONDS = 120;

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayAdapter paymentGatewayAdapter;
    private final PaymentService paymentService;

    /**
     * 주기적으로 PENDING 상태인 결제 트랜잭션의 상태를 동기화합니다.
     * 2분 간격으로 실행되며, 초기 지연도 2분입니다.
     */
    @Scheduled(fixedDelay = SYNC_INTERVAL_SECONDS, initialDelay = INITIAL_DELAY_SECONDS, timeUnit = TimeUnit.SECONDS)
    @Transactional
    public void syncPendingTransactions() {
        log.info("결제 상태 동기화 작업 시작");

        List<PaymentGatewayTransactionModel> pendingTransactions = fetchPendingTransactions();

        processPendingTransactions(pendingTransactions);

        log.info("결제 상태 동기화 작업 완료");
    }

    /**
     * PENDING 상태인 트랜잭션을 조회
     */
    private List<PaymentGatewayTransactionModel> fetchPendingTransactions() {
        List<PaymentGatewayTransactionModel> pendingTransactions = paymentRepository.findPendingTransactions();
        log.info("PENDING 상태 결제 건수: {}", pendingTransactions.size());
        return pendingTransactions;
    }

    /**
     * 각 PENDING 트랜잭션에 대해 동기화 수행
     */
    private void processPendingTransactions(List<PaymentGatewayTransactionModel> transactions) {
        for (PaymentGatewayTransactionModel transaction : transactions) {
            try {
                String transactionKey = transaction.getTransactionKey();
                syncTransactionStatus(transactionKey);
                log.debug("트랜잭션 동기화 성공: {}", transactionKey);
            } catch (Exception e) {
                log.error("트랜잭션 동기화 중 오류 발생: {} - {}", transaction.getTransactionKey(), e.getMessage(), e);
            }
        }
    }

    /**
     * 개별 트랜잭션의 상태를 PG사에서 조회하여 동기화
     */
    @Retry(name = "transactionRetry", fallbackMethod = "syncTransactionStatusFallback")
    @CircuitBreaker(name = "transactionCircuitBreaker", fallbackMethod = "syncTransactionStatusFallback")
    public void syncTransactionStatus(String transactionKey) {
        TransactionDetail detail = paymentGatewayAdapter.getTransaction(transactionKey);
        switch (detail.status()) {
            case COMPLETED -> paymentService.success(new PaymentCommand.Success(detail.orderId(), detail.transactionKey()));
            case FAILED -> paymentService.fail(new PaymentCommand.Fail(detail.orderId(), detail.transactionKey(), detail.reason()));
        }
    }

    /**
     * 트랜잭션 동기화 실패시 폴백 처리
     */
    public void syncTransactionStatusFallback(String transactionKey, Throwable t) {
        log.warn("트랜잭션 상태 동기화 실패: {} - {}", transactionKey, t.getMessage());
    }

}
