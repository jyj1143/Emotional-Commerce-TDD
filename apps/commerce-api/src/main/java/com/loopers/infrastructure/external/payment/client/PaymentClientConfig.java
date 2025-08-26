package com.loopers.infrastructure.external.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PaymentClientConfig {

    private final ObjectMapper objectMapper;
    /**
     * PG 시뮬레이터 에러 디코더
     */
    @Bean
    public ErrorDecoder pgSimulatorErrorDecoder() {
        return new PaymentClientErrorDecoder(objectMapper);
    }
}
