package com.loopers.infrastructure.external.payment.client;

import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayCommand.Payment;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Order;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.Transaction;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo.TransactionDetail;
import com.loopers.infrastructure.external.payment.dto.PgClientV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGatewaySimulator implements PaymentGatewayAdapter {

    private final PgV1Client pgV1Client;
    @Value("${pg-simulator.callback-url}")
    private String callbackUrl;
    @Value("${pg-simulator.store-id}")
    private String storeId;

    @Override
    public Transaction processPayment(Payment command) {

        PgClientV1Dto.PaymentRequest request = new PgClientV1Dto.PaymentRequest(
                command.orderId(),
                command.cardType().name(),
                command.cardNo(),
                command.amount(),
                callbackUrl
        );
        try {
            // PG 클라이언트 호출 및 응답 처리
            PaymentClientApiResponse<PgClientV1Dto.TransactionResponse> response = pgV1Client.processPayment(storeId, request);
            PgClientV1Dto.TransactionResponse transactionResponse = response.data();
            return transactionResponse.toTransaction();
        } catch (PaymentClientException e) {
            log.error("결제 처리 실패 - Status: {}, Type: {}, Message: {}",
                    e.getHttpStatus(), e.getErrorType(), e.getCustomMessage());
            // HTTP 상태에 따른 추가 처리
            if (e.getHttpStatus().is4xxClientError()) {
                // 클라이언트 에러 처리 - 일반적으로 재시도하지 않음
                throw new CoreException(ErrorType.BAD_REQUEST,
                        "결제 요청이 올바르지 않습니다: " + e.getCustomMessage());
            } else if (e.getHttpStatus().is5xxServerError()) {
                // 서버 에러 처리 - 재시도 가능한 에러
                throw new CoreException(ErrorType.INTERNAL_ERROR,
                        "결제 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
            // 기타 에러
            throw new CoreException(ErrorType.INTERNAL_ERROR,
                    "결제 처리 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Override
    public TransactionDetail getTransaction(String transactionKey) {
        try {
            PaymentClientApiResponse<PgClientV1Dto.TransactionDetailResponse> response = pgV1Client.getTransaction(storeId, transactionKey);
            return response.data().toTransactionDetail();
        } catch (PaymentClientException e) {
            log.error("결제 정보 조회 실패 - Status: {}, Type: {}, Message: {}",
                    e.getHttpStatus(), e.getErrorType(), e.getCustomMessage());
            // HTTP 상태에 따른 추가 처리
            if (e.getHttpStatus().is4xxClientError()) {
                // 클라이언트 에러 처리 - 일반적으로 재시도하지 않음
                throw new CoreException(ErrorType.BAD_REQUEST,
                        "결제 정보 요청이 올바르지 않습니다: " + e.getCustomMessage());
            } else if (e.getHttpStatus().is5xxServerError()) {
                // 서버 에러 처리 - 재시도 가능한 에러
                throw new CoreException(ErrorType.INTERNAL_ERROR,
                        "결제 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
            // 기타 에러
            throw new CoreException(ErrorType.INTERNAL_ERROR,
                    "결제 정보 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    @Override
    public Order getPaymentsByOrderId( String orderId) {
        try {
            PaymentClientApiResponse<PgClientV1Dto.OrderResponse> response = pgV1Client.getPaymentsByOrderId(storeId, orderId);
            PgClientV1Dto.OrderResponse orderResponse = response.data();
            return orderResponse.toOrder();
        }catch (PaymentClientException e){
            log.error("주문에 엮인 결제 정보 조회 실패 - Status: {}, Type: {}, Message: {}",
                    e.getHttpStatus(), e.getErrorType(), e.getCustomMessage());
            // HTTP 상태에 따른 추가 처리
            if (e.getHttpStatus().is4xxClientError()) {
                // 클라이언트 에러 처리 - 일반적으로 재시도하지 않음
                throw new CoreException(ErrorType.BAD_REQUEST,
                        "주문 정보 요청이 올바르지 않습니다: " + e.getCustomMessage());
            } else if (e.getHttpStatus().is5xxServerError()) {
                // 서버 에러 처리 - 재시도 가능한 에러
                throw new CoreException(ErrorType.INTERNAL_ERROR,
                        "결제 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
            // 기타 에러
            throw new CoreException(ErrorType.INTERNAL_ERROR,
                    "주문에 엮인 결제 정보 조회 중 알 수 없는 오류가 발생했습니다.");
        }
    }
}
