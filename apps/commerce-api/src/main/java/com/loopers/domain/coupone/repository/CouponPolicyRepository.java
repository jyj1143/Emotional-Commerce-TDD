package com.loopers.domain.coupone.repository;

import com.loopers.domain.coupone.entity.CouponPolicyModel;
import java.util.Optional;

public interface CouponPolicyRepository {

    CouponPolicyModel save(CouponPolicyModel couponPolicyModel);

    Optional<CouponPolicyModel> find(Long id);

    Optional<CouponPolicyModel> findWithLock(Long id);

    int decreaseRemainQuantity(Long id);

}
