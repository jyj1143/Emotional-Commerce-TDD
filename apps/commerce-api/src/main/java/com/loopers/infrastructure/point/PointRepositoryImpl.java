package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Transactional
    @Override
    public PointModel save(PointModel pointModel) {
        return pointJpaRepository.save(pointModel);
    }

    @Transactional
    @Override
    public int update(Long userId, Long point) {
        return pointJpaRepository.updatePoint(userId, point);
    }

    @Transactional
    @Override
    public int decrease(Long userId, Long point) {
        return pointJpaRepository.decrease(userId, point);
    }

    @Transactional
    @Override
    public int increase(Long userId, Long point) {
        return pointJpaRepository.increase(userId, point);
    }

    @Override
    public Optional<PointModel> findByUserId(Long userId) {
        return pointJpaRepository.findByRefUserId(userId);
    }

    @Override
    public Optional<PointModel> findWithLockByUserId(Long userId) {
        return pointJpaRepository.findWithLockByUserId(userId);
    }

    @Override
    public Boolean existsByUserId(Long userId) {
        return pointJpaRepository.existsByRefUserId(userId);
    }

}
