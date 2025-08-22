package com.loopers.domain.payment.service;

import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.entity.PaymentModel;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.repository.PaymentRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentInfo findByRefOrderId(Long orderId) {
        PaymentModel paymentModel = paymentRepository.findByOrderId(orderId).orElseThrow(
            () -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다.")
        );
        return PaymentInfo.of(
            paymentModel
        );
    }

    @Transactional
    public PaymentModel pay(PaymentCommand.Pay command) {
        PaymentModel paymentModel = PaymentModel.of(command.userId(),command.orderId(), command.method(), PaymentStatus.PENDING, command.amount());
        paymentRepository.save(paymentModel);
        paymentModel.complete();
        return paymentModel;
    }

    @Transactional
    public PaymentModel ready(PaymentCommand.Pay command) {
        PaymentModel paymentModel = PaymentModel.of(command.userId(),command.orderId(), command.method(), PaymentStatus.PENDING, command.amount());
        paymentRepository.save(paymentModel);
        return paymentModel;
    }

    @Transactional
    public PaymentGatewayTransactionModel readyPaymentGatewayTransaction(PaymentCommand.ReadyTransaction command) {
        PaymentGatewayTransactionModel paymentGatewayTransactionModel = PaymentGatewayTransactionModel.of(
            command.orderId(), command.paymentId(), command.transactionKey(), PaymentStatus.PENDING, command.amount(),
            command.cardType(), command.cardNumber()
        );
        return paymentRepository.save(paymentGatewayTransactionModel);
    }


    @Transactional
    public PaymentGatewayTransactionModel completePayment(Long orderId) {
        PaymentGatewayTransactionModel transaction = paymentRepository.findTrxByOrderId(orderId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));

        transaction.complete();

        // 연결된 PaymentModel도 완료 처리
        PaymentModel payment = paymentRepository.findById(transaction.getRefPaymentId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));
        payment.complete();

        return transaction;
    }

}
