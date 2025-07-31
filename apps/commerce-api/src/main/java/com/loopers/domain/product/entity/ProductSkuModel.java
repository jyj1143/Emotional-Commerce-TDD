package com.loopers.domain.product.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.vo.ProductOption;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// SKU (Stock Keeping Unit): 옵션이 조합된 실제 구매 단위
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "product_sku")
public class ProductSkuModel extends BaseEntity {

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "additional_price", nullable = false))
    Money additionalPrice; // 옵션에대한 추가 가격

    @Embedded
    ProductOption option;

    @Enumerated
    private SaleStatus saleStatus;

    @Column(name = "ref_product_id", nullable = false)
    private Long refProductId;

    private ProductSkuModel(Long additionalPrice, String optionType, String optionValue, SaleStatus saleStatus,
        Long refProductId) {
        this.additionalPrice = Money.of(additionalPrice);
        this.option = ProductOption.of(optionType, optionValue);
        this.saleStatus = saleStatus;
        this.refProductId = refProductId;
    }

    public static ProductSkuModel of(Long additionalPrice, String optionType, String optionValue
        , SaleStatus saleStatus, Long refProductId) {
        return new ProductSkuModel(additionalPrice, optionType, optionValue, saleStatus, refProductId);
    }
}
