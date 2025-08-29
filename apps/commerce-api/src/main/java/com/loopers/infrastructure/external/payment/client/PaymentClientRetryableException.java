package com.loopers.infrastructure.external.payment.client;

import org.springframework.http.HttpStatus;

public class PaymentClientRetryableException extends PaymentClientException {


    public PaymentClientRetryableException(HttpStatus httpStatus,
        String errorType, String customMessage) {
        super(httpStatus, errorType, customMessage);
    }

    public PaymentClientRetryableException(HttpStatus httpStatus, String errorType,
        String customMessage, Throwable cause) {
        super(httpStatus, errorType, customMessage, cause);
    }

    public PaymentClientRetryableException(HttpStatus httpStatus, String errorType) {
        super(httpStatus, errorType);
    }
}
