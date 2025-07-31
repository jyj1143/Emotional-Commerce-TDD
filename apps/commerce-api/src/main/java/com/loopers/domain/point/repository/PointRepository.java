package com.loopers.domain.point.repository;

import com.loopers.domain.point.PointModel;
import java.util.Optional;

public interface PointRepository {

    PointModel save(PointModel pointModel);

    PointModel update(Long userId, Long point);

    PointModel decrease(Long userId, Long point);

    PointModel increase(Long userId, Long point);

    Optional<PointModel> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

}
