package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.common.vo.Quantity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order_item")
public class OrderItemModel extends BaseEntity {

    @Embedded
    private Quantity quantity;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "purchase_price", nullable = false))
    private Money purchasePrice;

    @Column(name = "ref_product_sku_id", nullable = false)
    private Long refProductSkuId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_order_id")
    private OrderModel order;

    private OrderItemModel(Long quantity, Long purchasePrice,  Long refProductSkuId) {
        this.quantity = Quantity.of(quantity);
        this.purchasePrice = Money.of(purchasePrice);
        this.refProductSkuId = refProductSkuId;
    }

    public static OrderItemModel of(Long quantity, Long purchasePrice, Long refProductSkuId) {
        return new OrderItemModel(quantity, purchasePrice, refProductSkuId);
    }

    public Long calculateTotalPrice() {
        return purchasePrice.getAmount() * quantity.getQuantity();
    }

    void assignToOrderModel(OrderModel order) {
            this.order = order;
    }
}
