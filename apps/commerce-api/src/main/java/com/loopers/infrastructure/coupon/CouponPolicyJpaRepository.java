package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupone.entity.CouponPolicyModel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponPolicyJpaRepository extends JpaRepository<CouponPolicyModel, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cp FROM CouponPolicyModel cp WHERE cp.id = :id")
    Optional<CouponPolicyModel> findByIdWithLock(@Param("id") Long id);


    @Modifying
    @Query("UPDATE CouponPolicyModel cp "
        + "SET cp.remainQuantity.quantity = cp.remainQuantity.quantity - 1 "
        + "WHERE cp.id = :id  AND cp.remainQuantity.quantity > 0 ")
    int decreaseRemainQuantity(@Param("id") Long id);
}
