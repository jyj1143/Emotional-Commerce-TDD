package com.loopers.application.payment;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentGatewayAdapter paymentGatewayAdapter;

    public TransactionResult processPayment(PaymentCriteria.PgPay  criteria) {
        PaymentGatewayInfo.Transaction transaction = paymentGatewayAdapter.processPayment(criteria.toPaymentCommand());
        return TransactionResult.from(transaction);
    }

}
