package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentCriteria.Conclude;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec{

    private final PaymentFacade paymentFacade;

    @PostMapping
    @Override
    public ApiResponse<PaymentV1Dto.TransactionResponse> requestPayment(@RequestBody PaymentV1Dto.PaymentRequest request) {
        TransactionResult transaction = paymentFacade.processPayment(request.toPaymentCriteria());
        return ApiResponse.success(PaymentV1Dto.TransactionResponse.from(transaction));
    }

    @PostMapping("/callback")
    @Override
    public ApiResponse<?> callback(@RequestBody PaymentV1Dto.CallbackRequest request) {
        System.out.println("Payment callback received: " + request);
        PaymentCriteria.Conclude conclude = new PaymentCriteria.Conclude(
            request.transactionKey(),
            request.orderId(),
            request.cardType().toCardType(),
            request.cardNo(),
            request.amount(),
            request.status().toTransactionStatus(),
            request.reason()
        );
        paymentFacade.concludePayment(conclude);

        return ApiResponse.success();
    }
}
