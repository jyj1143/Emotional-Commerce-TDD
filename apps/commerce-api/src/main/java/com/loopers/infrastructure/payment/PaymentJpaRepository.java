package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.entity.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentModel, Long> {

}
