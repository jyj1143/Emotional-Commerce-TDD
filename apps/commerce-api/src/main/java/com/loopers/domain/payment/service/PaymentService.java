package com.loopers.domain.payment.service;

import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.dto.PaymentEvent;
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
    private final PaymentEventPublisher paymentEventPublisher;


    public PaymentInfo findByRefOrderId(Long orderId) {
        PaymentModel paymentModel = paymentRepository.findByOrderId(orderId).orElseThrow(
            () -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다.")
        );
        return PaymentInfo.of(
            paymentModel
        );
    }

    @Transactional
    public PaymentInfo pay(PaymentCommand.Pay command) {
        PaymentModel payment = paymentRepository.findByOrderId(command.orderId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 결제입니다."));
        payment.complete();
        return PaymentInfo.of(
            payment
        );
    }

    @Transactional
    public PaymentInfo ready(PaymentCommand.Ready command) {
        PaymentModel paymentModel = PaymentModel.of(command.userId(),command.orderId(), null, PaymentStatus.CREATED, command.amount());
        paymentRepository.save(paymentModel);
        return PaymentInfo.of(
            paymentModel
        );
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


    @Transactional
    public PaymentInfo success(PaymentCommand.Success command) {
        PaymentGatewayTransactionModel paymentTrx = paymentRepository.findTransactionByKey(
                command.transactionKey())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 트랜잭션 정보가 없습니다."));

        PaymentModel payment = paymentRepository.findByOrderId(command.orderId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보가 없습니다."));

        paymentTrx.complete();
        payment.complete();

        paymentEventPublisher.publish(PaymentEvent.PaymentSucceeded.from(payment, paymentTrx));

        return PaymentInfo.of(payment);
    }

    @Transactional
    public PaymentInfo fail(PaymentCommand.Fail command) {
        PaymentGatewayTransactionModel paymentTrx = paymentRepository.findTransactionByKey(
                command.transactionKey())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 트랜잭션 정보가 없습니다."));

        PaymentModel payment = paymentRepository.findByOrderId(command.orderId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보가 없습니다."));

        paymentTrx.fail(command.reason());
        payment.fail();

        // 결제 실패 이벤트 발행
        paymentEventPublisher.publish(PaymentEvent.PaymentFailed.from(payment, paymentTrx));

        return PaymentInfo.of(payment);
    }

}
