package com.loopers.infrastructure.dataplatform;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformSender {
    public void send(String topic, Object payload) {
        try {
            log.info("데이터 플랫폼으로 이벤트 전송 시작: topic=[{}], payload=[{}]", topic, payload);

            // 실제 환경에서는 외부 시스템 호출이 있을 것이므로 지연 시간 시뮬레이션
            Random random = new Random();
            int ms = random.nextInt(100, 1000);
            Thread.sleep(ms);

            log.info("데이터 플랫폼으로 이벤트 전송 완료: topic=[{}], 소요시간={}ms", topic, ms);
        } catch (InterruptedException e) {
            log.error("데이터 플랫폼 이벤트 전송 중 인터럽트 발생", e);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("데이터 플랫폼 이벤트 전송 중 인터럽트 발생");
        } catch (Exception e) {
            log.error("데이터 플랫폼 이벤트 전송 실패: topic=[{}], error=[{}]", topic, e.getMessage(), e);
            throw new IllegalStateException("데이터 플랫폼 이벤트 전송 실패: " + e.getMessage());
        }
    }
}
