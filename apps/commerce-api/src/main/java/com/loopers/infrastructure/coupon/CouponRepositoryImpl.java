package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.repository.CouponRepository;
import com.loopers.support.pagenation.PageResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public CouponModel save(CouponModel coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<CouponModel> find(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Long countByCouponPolicyId(Long id) {
        return couponJpaRepository.countByRefCouponPolicyId(id);
    }

    @Override
    public Optional<CouponModel> findByIdAndUserId(Long id, Long userId) {
        return couponJpaRepository.findByIdAndRefUserId(id, userId);
    }

    @Override
    public Optional<CouponModel> findWithLockByIdAndRefUserId(Long id, Long userId) {
        return couponJpaRepository.findWithLockByIdAndRefUserId(id, userId);
    }


    @Override
    public PageResult<CouponModel> findUserCoupon(Long userId, Integer page, Integer size) {
        Pageable pageable =
            PageRequest.of(page - 1
                , size
            );
        Page<CouponModel> pageResult = couponJpaRepository.findByRefUserId(userId, pageable);
        return PageResult.of(pageResult);
    }

}
