package com.loopers.domain.coupone.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.coupone.enums.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class CouponPolicyModelTest {

    @DisplayName("쿠폰 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("쿠폰정보가 정상적이면 성공한다.")
        @Test
        void given_validParameters_when_creatingCouponPolicyModel_thenCeateSuccessfully() {
            // Given
            String name = "Test Coupon";
            String description = "Test Description";
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = LocalDateTime.now().plusDays(10);
            DiscountType discountType = DiscountType.FIXED_AMOUNT;
            Long totalQuantity = 100L;
            Integer minimumOrderAmount = 1000;
            Integer maximumDiscountAmount = 500;
            BigDecimal discountValue = BigDecimal.valueOf(50);
            Long remainQuantity = 100L;

            // When
            CouponPolicyModel couponPolicyModel = CouponPolicyModel.of(name, description, startTime,
                endTime,
                discountType, totalQuantity, minimumOrderAmount, maximumDiscountAmount, discountValue,
                remainQuantity);

            // Then
            assertAll(
                () -> assertNotNull(couponPolicyModel),
                () -> assertEquals(name, couponPolicyModel.getName().getName()),
                () -> assertEquals(description, couponPolicyModel.getDescription()),
                () -> assertEquals(discountType, couponPolicyModel.getDiscountType()),
                () -> assertEquals(totalQuantity, couponPolicyModel.getTotalQuantity().getQuantity()),
                () -> assertEquals(remainQuantity, couponPolicyModel.getRemainQuantity().getQuantity()),
                () -> assertEquals(startTime, couponPolicyModel.getCouponPeriod().getStartTime()),
                () -> assertEquals(endTime, couponPolicyModel.getCouponPeriod().getEndTime()),
                () -> assertEquals(minimumOrderAmount, couponPolicyModel.getOrderAmountCondition().getMinimumOrderAmount()),
                () -> assertEquals(maximumDiscountAmount, couponPolicyModel.getOrderAmountCondition().getMaximumDiscountAmount()),
                () -> assertEquals(discountValue, couponPolicyModel.getDiscountValue().getDiscountValue())
            );
        }
    }

}
