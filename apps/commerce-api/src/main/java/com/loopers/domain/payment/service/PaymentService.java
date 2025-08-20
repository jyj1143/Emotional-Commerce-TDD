package com.loopers.domain.payment.service;

import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.entity.PaymentModel;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentModel pay(PaymentCommand.Pay command) {
        PaymentModel paymentModel = PaymentModel.of(command.orderId(), command.method(), PaymentStatus.PENDING, command.amount());
        paymentRepository.save(paymentModel);
        paymentModel.complete();
        return paymentModel;
    }

    @Transactional
    public PaymentModel ready(PaymentCommand.Pay command) {
        PaymentModel paymentModel = PaymentModel.of(command.orderId(), command.method(), PaymentStatus.PENDING, command.amount());
        paymentRepository.save(paymentModel);
        return paymentModel;
    }

}
