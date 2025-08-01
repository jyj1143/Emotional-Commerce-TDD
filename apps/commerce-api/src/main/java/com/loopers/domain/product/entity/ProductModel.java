package com.loopers.domain.product.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.vo.ProductName;
import com.loopers.domain.product.vo.SaleDate;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "product")
public class ProductModel extends BaseEntity {

    @Embedded
    private ProductName name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "sale_price", nullable = false))
    private Money salePrice;

    @Enumerated
    private SaleStatus saleStatus;

    private SaleDate saleDate;

    @Column(name = "ref_brand_id", nullable = false)
    private Long refBrandId;

    private  ProductModel(String name, Long salePrice, SaleStatus saleStatus, String saleDate, Long refBrandId) {
        this.name = ProductName.of(name);
        this.salePrice = Money.of((salePrice));
        this.saleStatus = saleStatus;
        this.saleDate = SaleDate.of((saleDate));
        this.refBrandId = refBrandId;
    }

    public static ProductModel of(String name, Long price, SaleStatus saleStatus, String saleDate, Long refBrandId) {
        return new ProductModel(name, price, saleStatus, saleDate, refBrandId);
    }

}
