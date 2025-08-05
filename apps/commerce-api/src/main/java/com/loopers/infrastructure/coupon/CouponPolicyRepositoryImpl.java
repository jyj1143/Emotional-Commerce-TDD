package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponPolicyRepositoryImpl implements CouponPolicyRepository {

    private final CouponPolicyJpaRepository couponPolicyJpaRepository;

    @Override
    public CouponPolicyModel save(CouponPolicyModel couponPolicyModel) {
        return couponPolicyJpaRepository.save(couponPolicyModel);
    }

    @Override
    public Optional<CouponPolicyModel> find(Long id) {
        return couponPolicyJpaRepository.findById(id);
    }

    @Override
    public Optional<CouponPolicyModel> findWithLock(Long id) {
        return couponPolicyJpaRepository.findByIdWithLock(id);
    }

    @Override
    public int decreaseRemainQuantity(Long id) {
        return couponPolicyJpaRepository.decreaseRemainQuantity(id);
    }

}
