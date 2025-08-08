package com.loopers.domain.coupone.dto;

import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponPolicyCommand(){

    public record Create(
        String name,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        DiscountType discountType,
        Long totalQuantity,
        Integer minimumOrderAmount,
        Integer maximumDiscountAmount,
        BigDecimal discountValue,
        Long remainQuantity
    ) {
        public CouponPolicyModel toEntity(){
            return  CouponPolicyModel.of(name, description, startTime, endTime, discountType, totalQuantity, minimumOrderAmount, maximumDiscountAmount, discountValue, remainQuantity);
        }

    }

}
