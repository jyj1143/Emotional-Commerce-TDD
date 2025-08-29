package com.loopers.infrastructure.external.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class PaymentClientErrorDecoder implements ErrorDecoder {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int SERVER_ERROR_MIN_STATUS = 500;
    private static final int SERVER_ERROR_MAX_STATUS = 599;
    private static final String LOG_PREFIX = "PG 시뮬레이터";

    private final ObjectMapper objectMapper;
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody = readResponseBody(response);
        if (responseBody == null) {
            return defaultErrorDecoder.decode(methodKey, response);
        }

        try {
            return processErrorResponse(methodKey, response, responseBody);
        } catch (IOException e) {
            log.error("{} 응답 파싱 실패", LOG_PREFIX, e);
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    private String readResponseBody(Response response) {
        try (Reader bodyReader = response.body().asReader(DEFAULT_CHARSET)) {
            return Util.toString(bodyReader);
        } catch (IOException e) {
            log.error("{} 응답 읽기 실패", LOG_PREFIX, e);
            return null;
        }
    }

    private Exception processErrorResponse(String methodKey, Response response, String responseBody) throws IOException {
        PaymentClientApiResponse<?> errorResponse = objectMapper.readValue(responseBody, PaymentClientApiResponse.class);

        if (errorResponse == null || errorResponse.meta() == null ||
            errorResponse.meta().result() != PaymentClientApiResponse.Metadata.Result.FAIL) {
            return defaultErrorDecoder.decode(methodKey, response);
        }

        String errorCode = errorResponse.meta().errorCode();
        String errorMessage = errorResponse.meta().message();
        HttpStatus httpStatus = HttpStatus.valueOf(response.status());

        logErrorDetails(methodKey, httpStatus, errorCode, errorMessage);

        return createAppropriateException(httpStatus, errorCode, errorMessage);
    }

    private void logErrorDetails(String methodKey, HttpStatus httpStatus, String errorCode, String errorMessage) {
        log.error("{} API 호출 실패 - Method: {}, Status: {}, Message: {}",
            LOG_PREFIX, methodKey, httpStatus, errorMessage);
        log.warn("{} 에러 - 코드: {}, 메시지: {}", LOG_PREFIX, errorCode, errorMessage);
    }

    private Exception createAppropriateException(HttpStatus httpStatus, String errorCode, String errorMessage) {
        int statusCode = httpStatus.value();

        if (isServerError(statusCode)) {
            return new PaymentClientRetryableException(httpStatus, errorCode, errorMessage);
        } else {
            return new PaymentClientException(httpStatus, errorCode, errorMessage);
        }
    }

    private boolean isServerError(int statusCode) {
        return statusCode >= SERVER_ERROR_MIN_STATUS && statusCode < SERVER_ERROR_MAX_STATUS;
    }
}
