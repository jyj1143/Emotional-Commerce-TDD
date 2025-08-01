package com.loopers.domain.inventory;

import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InventoryModelTest {
    @DisplayName("재고 모델을 생성할 때, ")
    @Nested
    class Create {
        @DisplayName("재고 수량이 음수이면, CoreException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {
            -1,-100
        })
        void given_invalidQuantity_when_createInventory_then_throwCoreException(Long invalidQuantity) {
            // given
            ProductSkuModel productSkuModel = ProductSkuModel.of(0L, "색상", "RED"
                , SaleStatus.ON_SALE, null);
            Long refProductId = productSkuModel.getRefProductId();
            // when
            // then
            assertThrows(CoreException.class, () -> {
                InventoryModel.of(invalidQuantity, refProductId);
            });
        }
    }
}
