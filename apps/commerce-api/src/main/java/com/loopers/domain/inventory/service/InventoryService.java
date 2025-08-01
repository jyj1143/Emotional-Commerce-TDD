package com.loopers.domain.inventory.service;

import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void create(InventoryCommand.Create command) {
        InventoryModel inventory = InventoryModel.of(
            command.productSkuId(),
            command.quantity()
        );
        if (inventoryRepository.isExists(command.productSkuId())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 재고입니다.");
        }
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void decrease(InventoryCommand.DecreaseStock command) {
        InventoryModel inventoryModel = inventoryRepository.find(command.productSkuId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고가 존재하지 않습니다."));

        if (inventoryModel.getQuantity().getQuantity() < command.quantity()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");
        }
        inventoryRepository.decrementStock(command.productSkuId(), command.quantity());
    }

    @Transactional
    public void increase(InventoryCommand.IncreaseStock command) {
        InventoryModel inventoryModel = inventoryRepository.find(command.productSkuId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고가 존재하지 않습니다."));

        inventoryRepository.incrementStock(command.productSkuId(), command.quantity());
    }

    public InventoryModel getInventory(InventoryCommand.GetInventory command) {
        return inventoryRepository.find(command.productSkuId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "재고가 존재하지 않습니다."));
    }

}
