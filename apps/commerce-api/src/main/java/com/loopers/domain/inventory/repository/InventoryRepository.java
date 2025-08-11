package com.loopers.domain.inventory.repository;

import com.loopers.domain.inventory.InventoryModel;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    InventoryModel save(InventoryModel inventory);

    Optional<InventoryModel> find(Long productSkuId);

    Optional<InventoryModel> findWithLock(Long productSkuId);

    Boolean isExists(Long productSkuId);

    int incrementStock(Long productSkuId, Long quantity);

    int decrementStock(Long productSkuId, Long quantity);
}
