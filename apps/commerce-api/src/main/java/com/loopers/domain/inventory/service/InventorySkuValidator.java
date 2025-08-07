package com.loopers.domain.inventory.service;

import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.product.dto.sku.ProductSkuCommand.OrderSku;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventorySkuValidator {

    private final InventoryRepository inventoryRepository;

    public void validateSkuStock(List<OrderSku> skuList){
        for (OrderSku orderSku : skuList) {
            // 재고 시스템에서 해당 SKU의 재고 확인
            InventoryModel sku = inventoryRepository.findWithLock(orderSku.skuId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
            Long availableQuantity = sku.getQuantity().getQuantity();
            // 주문 수량이 재고보다 많은 경우 예외 발생
            if (availableQuantity < orderSku.quantity()) {
                throw new CoreException(ErrorType.BAD_REQUEST,
                    String.format("상품 SKU(ID: %d)의 재고가 부족합니다. 요청: %d, 가능: %d",
                        orderSku.skuId(), orderSku.quantity(), availableQuantity));
            }
        }
    }

}
