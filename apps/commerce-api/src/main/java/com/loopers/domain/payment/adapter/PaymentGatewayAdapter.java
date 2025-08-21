package com.loopers.domain.payment.adapter;

public interface PaymentGatewayAdapter {

    // 결제 요청
    PaymentGatewayInfo.Transaction processPayment( PaymentGatewayCommand.Payment command);

    // 결제 정보 확인
    PaymentGatewayInfo.TransactionDetail getTransaction( String transactionKey);

    // 주문에 엮인 결제 정보 조회
    PaymentGatewayInfo.Order getPaymentsByOrderId( String orderId);
}
