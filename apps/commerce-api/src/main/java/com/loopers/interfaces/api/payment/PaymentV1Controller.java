package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentFacade;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentV1Controller implements PaymentV1ApiSpec{

    private final PaymentFacade paymentFacade;

    @PostMapping
    @Override
    public ApiResponse<PaymentV1Dto.TransactionResponse> requestPayment(PaymentV1Dto.PaymentRequest request) {
        TransactionResult transaction = paymentFacade.processPayment(request.toPaymentCriteria());
        return ApiResponse.success(PaymentV1Dto.TransactionResponse.from(transaction));
    }
}
