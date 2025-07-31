package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PointJpaRepository extends JpaRepository<PointModel, Long> {
    Optional<PointModel> findByRefUserId(Long userId);

    @Modifying
    @Query("UPDATE PointModel p "
        + "SET p.amount.amount = p.amount.amount + :point "
        + "WHERE p.refUserId = :userId ")
    PointModel updatePoint(Long userId, Long point);

    @Modifying
    @Query("UPDATE PointModel p "
        + "SET p.amount.amount = p.amount.amount - :point "
        + "WHERE p.refUserId = :userId ")
    PointModel decrease(Long userId, Long point);

    @Modifying
    @Query("UPDATE PointModel p "
        + "SET p.amount.amount = p.amount.amount + :point "
        + "WHERE p.refUserId = :userId ")
    PointModel increase(Long userId, Long point);

    boolean existsByRefUserId(Long userId);

}
