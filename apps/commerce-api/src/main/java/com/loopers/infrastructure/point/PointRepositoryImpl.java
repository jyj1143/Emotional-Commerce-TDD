package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public PointModel save(PointModel pointModel) {
        return pointJpaRepository.save(pointModel);
    }

    @Override
    public PointModel update(Long userId, Long point) {
        return pointJpaRepository.updatePoint(userId, point);
    }

    @Override
    public PointModel decrease(Long userId, Long point) {
        return pointJpaRepository.decrease(userId, point);
    }

    @Override
    public PointModel increase(Long userId, Long point) {
        return pointJpaRepository.increase(userId, point);
    }

    @Override
    public Optional<PointModel> findByUserId(Long userId) {
        return pointJpaRepository.findByRefUserId(userId);
    }

    @Override
    public Boolean existsByUserId(Long userId) {
        return pointJpaRepository.existsByRefUserId(userId);
    }

}
