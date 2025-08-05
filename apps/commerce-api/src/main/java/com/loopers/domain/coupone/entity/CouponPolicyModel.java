package com.loopers.domain.coupone.entity;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.common.vo.Quantity;
import com.loopers.domain.coupone.enums.DiscountType;
import com.loopers.domain.coupone.vo.CouponPeriod;
import com.loopers.domain.coupone.vo.CouponPolicyName;
import com.loopers.domain.coupone.vo.DiscountValue;
import com.loopers.domain.coupone.vo.OrderAmountCondition;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "coupon_policy")
public class CouponPolicyModel extends BaseEntity {

    @Column(nullable = false)
    private CouponPolicyName name;

    @Column
    private String description;

    @Embedded
    private CouponPeriod couponPeriod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "total_quantity", nullable = false))
    private Quantity totalQuantity;

    @Embedded
    private OrderAmountCondition orderAmountCondition;

    @Embedded
    private DiscountValue discountValue;

    @Embedded
    @AttributeOverride(name = "quantity", column = @Column(name = "remain_quantity", nullable = false))
    private Quantity remainQuantity;

    private CouponPolicyModel(String name, String description, LocalDateTime startTime, LocalDateTime endTime,
        DiscountType discountType, Long totalQuantity, Integer minimumOrderAmount, Integer maximumDiscountAmount
        , BigDecimal discountValue, Long remainQuantity) {

        if (discountType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "할인 유형은 필수 값입니다.");
        }

        this.name = CouponPolicyName.of(name);
        this.description = description;
        this.couponPeriod = CouponPeriod.of(startTime, endTime);
        this.discountType = discountType;
        this.totalQuantity = Quantity.of(totalQuantity);
        this.orderAmountCondition = OrderAmountCondition.of(minimumOrderAmount, maximumDiscountAmount);
        this.discountValue = DiscountValue.of(discountValue);
        this.remainQuantity = Quantity.of(remainQuantity);
    }

    public static CouponPolicyModel of(String name, String description, LocalDateTime startTime, LocalDateTime endTime,
        DiscountType discountType, Long totalQuantity, Integer minimumOrderAmount, Integer maximumDiscountAmount
        , BigDecimal discountValue, Long remainQuantity) {
        return new CouponPolicyModel(name, description, startTime, endTime, discountType, totalQuantity, minimumOrderAmount,
            maximumDiscountAmount, discountValue, remainQuantity);
    }

}
