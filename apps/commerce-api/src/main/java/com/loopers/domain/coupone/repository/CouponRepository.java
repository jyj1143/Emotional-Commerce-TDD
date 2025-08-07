package com.loopers.domain.coupone.repository;

import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.support.pagenation.PageResult;
import java.util.Optional;

public interface CouponRepository {

    CouponModel save(CouponModel coupon);

    Optional<CouponModel> find(Long id);

    Long countByCouponPolicyId(Long id);

    Optional<CouponModel> findByIdAndUserId(Long id, Long userId);

    Optional<CouponModel> findWithLockByIdAndRefUserId(Long id, Long userId);

    PageResult<CouponModel> findUserCoupon(Long userId, Integer page, Integer size);
}
