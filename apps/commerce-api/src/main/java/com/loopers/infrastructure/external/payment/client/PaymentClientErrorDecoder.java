package com.loopers.infrastructure.external.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class PaymentClientErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());
        String errorType = determineErrorType(httpStatus);
        String customMessage = extractErrorMessage(response);

        log.error("PG Simulator API 호출 실패 - Method: {}, Status: {}, Message: {}",
                methodKey, httpStatus, customMessage);

        return new PaymentClientException(httpStatus, errorType, customMessage);
    }

    private String determineErrorType(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "PAYMENT_BAD_REQUEST";
            case UNAUTHORIZED -> "PAYMENT_UNAUTHORIZED";
            case FORBIDDEN -> "PAYMENT_FORBIDDEN";
            case NOT_FOUND -> "PAYMENT_NOT_FOUND";
            case REQUEST_TIMEOUT -> "PAYMENT_TIMEOUT";
            case TOO_MANY_REQUESTS -> "PAYMENT_RATE_LIMITED";
            case INTERNAL_SERVER_ERROR -> "PAYMENT_INTERNAL_ERROR";
            case BAD_GATEWAY -> "PAYMENT_BAD_GATEWAY";
            case SERVICE_UNAVAILABLE -> "PAYMENT_SERVICE_UNAVAILABLE";
            case GATEWAY_TIMEOUT -> "PAYMENT_GATEWAY_TIMEOUT";
            default -> "PAYMENT_UNKNOWN_ERROR";
        };
    }

    private String extractErrorMessage(Response response) {
        try {
            if (response.body() != null) {
                String body = new String(response.body().asInputStream().readAllBytes());
                var errorResponse = objectMapper.readTree(body);
                if (errorResponse.has("error")) {
                    return errorResponse.get("error").asText();
                }
                return body;
            }
        } catch (IOException e) {
            log.warn("에러 응답 파싱 실패", e);
        }
        return "PG Simulator 호출 실패 (HTTP " + response.status() + ")";
    }

}
