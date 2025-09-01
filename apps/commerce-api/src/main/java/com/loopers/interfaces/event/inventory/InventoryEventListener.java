package com.loopers.interfaces.event.inventory;

import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.inventory.service.InventoryService;
import com.loopers.domain.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {
    private final InventoryService inventoryService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreatedEvent(OrderEvent.Created event) {
        // 재고 감소
        event.items().forEach(item ->
            inventoryService.decrease(new InventoryCommand.DecreaseStock(
                item.productSkuId(),
                item.quantity()))
        );
    }

    /**
     * 주문 취소 이벤트 처리
     * - 재고 복원
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCancelledEvent(OrderEvent.Cancelled event) {
        event.items().forEach(item ->
            inventoryService.increase(new InventoryCommand.IncreaseStock(
                item.productSkuId(),
                item.quantity()))
        );

        log.info("주문 취소로 인한 재고 복원 완료 - 주문 ID: {}", event.orderId());
    }
}
