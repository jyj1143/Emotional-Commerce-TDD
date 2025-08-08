package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupone.entity.CouponModel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponJpaRepository extends JpaRepository<CouponModel, Long> {

    Long countByRefCouponPolicyId(Long id);

    Optional<CouponModel> findByIdAndRefUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponModel c WHERE c.id = :id AND c.refUserId = :userId")
    Optional<CouponModel> findWithLockByIdAndRefUserId(@Param("id") Long id, @Param("userId") Long userId);

    Page<CouponModel> findByRefUserId(Long userId, Pageable pageable);
}
