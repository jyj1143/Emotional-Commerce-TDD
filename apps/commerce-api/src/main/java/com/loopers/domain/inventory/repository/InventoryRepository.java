package com.loopers.domain.inventory.repository;

import com.loopers.domain.inventory.InventoryModel;
import java.util.Optional;

public interface InventoryRepository {

    InventoryModel save(InventoryModel inventory);

    Optional<InventoryModel> find(Long productSkuId);

    Boolean isExists(Long productSkuId);

    InventoryModel incrementStock(Long productSkuId, Long quantity);

    InventoryModel decrementStock(Long productSkuId, Long quantity);
}
