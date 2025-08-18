package com.loopers.domain.product.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.PositiveCount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "product_signal",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ref_product_id"})
        })
public class ProductSignalModel extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false)
    private Long refProductId;

    @Embedded
    @AttributeOverride(name = "count", column = @Column(name = "like_count", nullable = false))
    private PositiveCount likeCount;

    private ProductSignalModel(Long refProductId, Long likeCount) {
        this.refProductId = refProductId;
        this.likeCount = PositiveCount.of(likeCount);
    }

    public static ProductSignalModel of(Long refProductId, Long likeCount) {
        return new ProductSignalModel(refProductId, likeCount);
    }

    void increaseLikeCount() {
        likeCount.plus(1L);
    }

    void decreaseLikeCount() {
        likeCount.minus(1L);
    }

}
