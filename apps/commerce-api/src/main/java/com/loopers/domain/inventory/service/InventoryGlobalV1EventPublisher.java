package com.loopers.domain.inventory.service;

import com.loopers.domain.inventory.dto.InventoryGlobalEvent;

public interface InventoryGlobalV1EventPublisher {

    void publish(InventoryGlobalEvent.StockChanged event);
}

