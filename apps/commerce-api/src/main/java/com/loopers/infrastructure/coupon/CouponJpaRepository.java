package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupone.entity.CouponModel;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<CouponModel, Long> {

    Long countByRefCouponPolicyId(Long id);

    Optional<CouponModel> findByIdAndRefUserId(Long id, Long userId);

    Page<CouponModel> findByRefUserId(Long userId, Pageable pageable);
}
