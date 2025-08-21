package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGatewayTransactionJpaRepository extends JpaRepository<PaymentGatewayTransactionModel, Long> {
    Optional<PaymentGatewayTransactionModel> findByTransactionKey(String transactionKey);
}
