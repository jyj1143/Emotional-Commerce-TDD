package com.loopers.domain.point.repository;

import com.loopers.domain.point.PointModel;
import java.util.Optional;

public interface PointRepository {

    PointModel save(PointModel pointModel);

    int update(Long userId, Long point);

    int decrease(Long userId, Long point);

    int increase(Long userId, Long point);

    Optional<PointModel> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);

}
