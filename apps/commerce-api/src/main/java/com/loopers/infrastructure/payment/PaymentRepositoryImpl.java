package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.entity.PaymentModel;
import com.loopers.domain.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentGatewayTransactionJpaRepository paymentGatewayTransactionJpaRepository;

    public List<PaymentGatewayTransactionModel> findPendingTransactions() {
        return paymentGatewayTransactionJpaRepository.findPendingTransactions();
    }

    public List<PaymentGatewayTransactionModel> findPendingTransactionsBefore(LocalDateTime time) {
        return paymentGatewayTransactionJpaRepository.findPendingTransactionsBefore(time);
    }

    @Override
    public Optional<PaymentModel> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    @Override
    public PaymentModel findByRefOrderId(Long orderId) {
        return paymentJpaRepository.findByRefOrderId(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found for order ID: " + orderId));
    }

    @Override
    public PaymentModel save(PaymentModel payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public PaymentGatewayTransactionModel save(PaymentGatewayTransactionModel payment) {
        return paymentGatewayTransactionJpaRepository.save(payment);
    }

    @Override
    public Optional<PaymentGatewayTransactionModel> findTransactionByKey(String transactionKey) {
        return paymentGatewayTransactionJpaRepository.findByTransactionKey(transactionKey);
    }

}
