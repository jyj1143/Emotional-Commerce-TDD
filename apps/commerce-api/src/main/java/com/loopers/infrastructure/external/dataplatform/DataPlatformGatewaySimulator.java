package com.loopers.infrastructure.external.dataplatform;

import com.loopers.domain.dataplatform.DataPlatformGatewayAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformGatewaySimulator implements DataPlatformGatewayAdapter {

    private final DataPlatformSender dataPlatformSender;

    @Override
    public void sendEvent(String topic, Object payload) {
        log.info("데이터 플랫폼 게이트웨이 시뮬레이터 호출: topic=[{}]", topic);
        dataPlatformSender.send(topic, payload);
    }
}
