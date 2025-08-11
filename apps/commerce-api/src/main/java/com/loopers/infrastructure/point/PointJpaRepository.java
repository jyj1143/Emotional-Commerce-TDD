package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointModel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointJpaRepository extends JpaRepository<PointModel, Long> {
    Optional<PointModel> findByRefUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PointModel p WHERE p.refUserId = :userId")
    Optional<PointModel> findWithLockByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PointModel p "
        + "SET p.amount.amount = p.amount.amount + :point "
        + "WHERE p.refUserId = :userId ")
    int updatePoint(Long userId, Long point);

    @Modifying
    @Query("UPDATE PointModel p "
        + "SET p.amount.amount = p.amount.amount - :point "
        + "WHERE p.refUserId = :userId "
        + "AND p.amount.amount >= :point ")
    int decrease(Long userId, Long point);

    @Modifying
    @Query("UPDATE PointModel p "
        + "SET p.amount.amount = p.amount.amount + :point "
        + "WHERE p.refUserId = :userId ")
    int increase(Long userId, Long point);

    boolean existsByRefUserId(Long userId);

}
