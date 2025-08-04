package com.loopers.domain.inventory;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Quantity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "inventory")
public class InventoryModel extends BaseEntity {

    @Embedded
    Quantity quantity;

    @Column(name = "ref_product_sku_id", nullable = false)
    private Long refProductSkuId;

    private InventoryModel(Long quantity, Long refProductId) {
        this.quantity = Quantity.of(quantity);
        this.refProductSkuId = refProductId;
    }

    public static InventoryModel of(Long quantity, Long refProductId) {
        return new InventoryModel(quantity, refProductId);
    }

}
