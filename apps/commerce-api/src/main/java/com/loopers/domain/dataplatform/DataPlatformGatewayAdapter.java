package com.loopers.domain.dataplatform;

/**
 * 데이터 플랫폼 API 클라이언트 인터페이스
 */
public interface DataPlatformGatewayAdapter {
    /**
     * 지정된 토픽으로 이벤트 데이터를 전송
     *
     * @param topic 이벤트 토픽
     * @param payload 전송할 데이터
     */
    void sendEvent(String topic, Object payload);
}

