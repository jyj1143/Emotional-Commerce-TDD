package com.loopers.infrastructure.inventory;

import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public InventoryModel save(InventoryModel inventory) {
        return inventoryJpaRepository.save(inventory);
    }

    @Override
    public Optional<InventoryModel> find(Long productSkuId) {
        return inventoryJpaRepository.findByRefProductSkuId(productSkuId);
    }

    @Override
    public Boolean isExists(Long productSkuId) {
        return inventoryJpaRepository.existsByRefProductSkuId(productSkuId);
    }

    @Override
    public InventoryModel incrementStock(Long productSkuId, Long quantity) {
        return inventoryJpaRepository.incrementStock(productSkuId, quantity);
    }

    @Override
    public InventoryModel decrementStock(Long productSkuId, Long quantity) {
        return inventoryJpaRepository.decreaseStock(productSkuId, quantity);
    }

}
