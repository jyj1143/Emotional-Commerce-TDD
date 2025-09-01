package com.loopers.domain.dataplatform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 데이터 플랫폼으로 이벤트를 전송하는 서비스
 */
@Component
@RequiredArgsConstructor
public class DataPlatformSender {
    private final DataPlatformGatewayAdapter dataPlatformGatewayAdapter;

    /**
     * 이벤트 토픽과 페이로드를 데이터 플랫폼으로 전송
     *
     * @param topic 이벤트 토픽 (e.g. "order.created", "order.completed")
     * @param payload 전송할 데이터 페이로드
     */
    public void send(String topic, Object payload) {
        dataPlatformGatewayAdapter.sendEvent(topic, payload);
    }
}
