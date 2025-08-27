package com.loopers.interfaces.event.inventory;

import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.inventory.service.InventoryService;
import com.loopers.domain.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
}
