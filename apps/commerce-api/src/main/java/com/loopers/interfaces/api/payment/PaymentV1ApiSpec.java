package com.loopers.interfaces.api.payment;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Payment V1 API", description = "Payment management API for version 1")
public interface PaymentV1ApiSpec {
    @Operation(
            summary = "결제 요청",
            description = "결제 요청합니다."
    )
    ApiResponse<PaymentV1Dto.TransactionResponse> requestPayment(
            @RequestBody PaymentV1Dto.PaymentRequest request);

    @Operation(
            summary = "결제 콜백 URL",
            description = "결제 콜백 URL"
    )
    ApiResponse<?> callback(
            @RequestBody PaymentV1Dto.CallbackRequest request);

}
