package com.loopers.domain.payment.repository;

import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.entity.PaymentModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    List<PaymentGatewayTransactionModel> findPendingTransactions();

    List<PaymentGatewayTransactionModel> findPendingTransactionsBefore(LocalDateTime time);

    Optional<PaymentModel> findById(Long id);

    PaymentModel findByRefOrderId(Long orderId);

    PaymentModel save(PaymentModel payment);

    PaymentGatewayTransactionModel save(PaymentGatewayTransactionModel payment);

    Optional<PaymentGatewayTransactionModel> findTransactionByKey(String transactionKey);

}
