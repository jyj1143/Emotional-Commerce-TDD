package com.loopers.infrastructure.external.payment.client;


public record PaymentClientApiResponse<T>(Metadata meta, T data) {
    public record Metadata(Result result, String errorCode, String message) {
        public enum Result {
            SUCCESS, FAIL
        }
    }
}
