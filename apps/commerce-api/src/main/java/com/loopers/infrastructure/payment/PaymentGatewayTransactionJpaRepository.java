package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentGatewayTransactionJpaRepository extends JpaRepository<PaymentGatewayTransactionModel, Long> {
    Optional<PaymentGatewayTransactionModel> findByTransactionKey(String transactionKey);

    Optional<PaymentGatewayTransactionModel> findByRefOrderId(Long orderId);

    /**
     * PENDING 상태인 트랜잭션 목록을 조회합니다.
     * 5분 이상 지난 트랜잭션만 조회하려면 created_at 조건을 추가할 수 있습니다.
     */
    @Query("SELECT p FROM PaymentGatewayTransactionModel p WHERE p.paymentStatus = 'PENDING'")
    List<PaymentGatewayTransactionModel> findPendingTransactions();

    /**
     * 특정 시간 이전에 생성된 PENDING 상태 트랜잭션만 조회
     */
    @Query("SELECT p FROM PaymentGatewayTransactionModel p WHERE p.paymentStatus = 'PENDING' AND p.createdAt < :time")
    List<PaymentGatewayTransactionModel> findPendingTransactionsBefore(LocalDateTime time);

}
