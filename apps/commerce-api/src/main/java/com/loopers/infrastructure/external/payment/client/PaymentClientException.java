package com.loopers.infrastructure.external.payment.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaymentClientException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorType;
    private final String customMessage;

    public PaymentClientException(HttpStatus httpStatus, String errorType, String customMessage) {
        super(customMessage != null ? customMessage : errorType);
        this.httpStatus = httpStatus;
        this.errorType = errorType;
        this.customMessage = customMessage;
    }

    public PaymentClientException(HttpStatus httpStatus, String errorType, String customMessage, Throwable cause) {
        super(customMessage != null ? customMessage : errorType, cause);
        this.httpStatus = httpStatus;
        this.errorType = errorType;
        this.customMessage = customMessage;
    }

    public PaymentClientException(HttpStatus httpStatus, String errorType) {
        this(httpStatus, errorType, null);
    }
}
