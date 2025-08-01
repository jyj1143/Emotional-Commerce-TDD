package com.loopers.domain.payment.repository;

import com.loopers.domain.payment.entity.PaymentModel;

public interface PaymentRepository {
    PaymentModel save(PaymentModel payment);
}
