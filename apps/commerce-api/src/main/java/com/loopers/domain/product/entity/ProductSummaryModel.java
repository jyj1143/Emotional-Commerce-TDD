package com.loopers.domain.product.entity;


import com.loopers.domain.BaseEntity;
import com.loopers.domain.brand.vo.BrandName;
import com.loopers.domain.common.vo.Money;
import com.loopers.domain.common.vo.PositiveCount;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.vo.ProductName;
import com.loopers.domain.product.vo.SaleDate;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품의 요약 정보를 나타내는 엔티티입니다. 상품 상세 정보와는 별도로 상품의 요약된 형태로 저장됩니다.
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "product_summary")
public class ProductSummaryModel extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false)
    private Long refProductId;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "product_name", nullable = false))
    private ProductName productName;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "sale_price", nullable = false))
    private Money salePrice;

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;

    private SaleDate saleDate;

    @Column(name = "ref_brand_id", nullable = false)
    private Long refBrandId;

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "brand_name", nullable = false))
    private BrandName brandName;

    @Column(name = "ref_like_id", nullable = false)
    private Long refLikeId;

    @Embedded
    @AttributeOverride(name = "count", column = @Column(name = "like_count", nullable = false))
    private PositiveCount likeCount;

    private ProductSummaryModel(Long refProductId, String productName, Long salePrice,
        SaleStatus saleStatus, String saleDate, Long refBrandId, String brandName, Long refLikeId,
        Long likeCount) {
        this.refProductId = refProductId;
        this.productName = ProductName.of(productName);
        this.salePrice = Money.of(salePrice);
        this.saleStatus = saleStatus;
        this.saleDate = SaleDate.of((saleDate));
        this.refBrandId = refBrandId;
        this.brandName = BrandName.of(brandName);
        this.refLikeId = refLikeId;
        this.likeCount = PositiveCount.of(likeCount);
    }

    public static ProductSummaryModel of(Long refProductId, String productName, Long salePrice,
        SaleStatus saleStatus, String saleDate, Long refBrandId, String brandName, Long refLikeId,
        Long likeCount) {
        return new ProductSummaryModel(refProductId, productName, salePrice, saleStatus, saleDate,
            refBrandId, brandName, refLikeId, likeCount);
    }

    void increaseLikeCount() {
        likeCount.plus(1L);
    }

    void decreaseLikeCount() {
        likeCount.minus(1L);
    }

}
