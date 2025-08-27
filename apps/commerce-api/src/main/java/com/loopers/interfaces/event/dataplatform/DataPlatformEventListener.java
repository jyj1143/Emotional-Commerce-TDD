package com.loopers.interfaces.event.dataplatform;

import com.loopers.domain.dataplatform.DataPlatformSender;
import com.loopers.domain.order.dto.OrderEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformEventListener{

    private final DataPlatformSender dataPlatformSender;

    /**
     * 주문 생성 이벤트를 데이터 플랫폼으로 전송
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderEvent.Created event) {
        try {
            log.info("주문 생성 이벤트 데이터 플랫폼 전송: orderId=[{}]", event.orderId());

            Map<String, Object> payload = new HashMap<>();
            payload.put("eventType", "ORDER_CREATED");
            payload.put("orderId", event.orderId());
            payload.put("userId", event.userId());
            payload.put("couponId", event.couponId());
            // 주문 상품 목록 추가
            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (OrderEvent.Created.OrderItem item : event.items()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productSkuId", item.productSkuId());
                itemMap.put("quantity", item.quantity());
                itemsList.add(itemMap);
            }
            payload.put("items", itemsList);
            dataPlatformSender.send("order.created", payload);
        } catch (Exception e) {
            log.error("주문 생성 이벤트 데이터 플랫폼 전송 실패: orderId=[{}], error=[{}]",
                event.orderId(), e.getMessage(), e);
        }
    }
}
