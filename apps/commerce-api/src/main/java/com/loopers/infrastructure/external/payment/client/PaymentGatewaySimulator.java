package com.loopers.infrastructure.external.payment.client;

import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayCommand.Payment;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Order;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Transaction;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.TransactionDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewaySimulator implements PaymentGatewayAdapter {

    private final PgV1Client pgV1Client;

    @Override
    public TransactionDetail processPayment(String userId, Payment command) {

        return null;
    }

    @Override
    public Transaction getTransaction(String userId, String transactionKey) {
        return null;
    }

    @Override
    public Order getPaymentsByOrderId(String userId, String orderId) {
        return null;
    }
}
