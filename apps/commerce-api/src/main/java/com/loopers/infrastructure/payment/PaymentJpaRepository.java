package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.entity.PaymentModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentModel, Long> {
    Optional<PaymentModel> findByRefOrderId(Long orderId);
}
