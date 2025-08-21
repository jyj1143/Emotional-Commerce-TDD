package com.loopers.infrastructure.external.payment.client;

import com.loopers.infrastructure.external.payment.dto.PgClientV1Dto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${pg.service-url}", name = "pgV1Client", configuration = PaymentClientConfig.class)
public interface PgV1Client {

    @PostMapping("/api/v1/payments")
    PaymentClientApiResponse<PgClientV1Dto.TransactionResponse> processPayment(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PgClientV1Dto.PaymentRequest request);

    @GetMapping("/api/v1/payments/{transactionKey}")
    PaymentClientApiResponse<PgClientV1Dto.TransactionDetailResponse> getTransaction(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable("transactionKey") String transactionKey
    );

    @GetMapping("/api/v1/payments")
    PaymentClientApiResponse<PgClientV1Dto.OrderResponse> getPaymentsByOrderId(
            @RequestHeader("X-USER-ID") String userId,
            @RequestParam("orderId") String orderId
    );
}
