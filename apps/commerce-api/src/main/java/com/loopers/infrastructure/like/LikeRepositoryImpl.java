package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.repository.LikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public LikeModel save(LikeModel like) {
        return likeJpaRepository.save(like);
    }

    @Override
    public void deleteWithLock(Long userId,  Long targetId,  LikeType likeType) {
        likeJpaRepository.deleteWithLock(userId, targetId, likeType);
    }

    @Override
    public Long count(Long target, LikeType type) {
        return likeJpaRepository.countByTargetIdAndLikeType(target, type);
    }

    @Override
    public boolean isExists(Long userId, Long target, LikeType type) {
        return likeJpaRepository.existsByUserIdAndTargetIdAndLikeType(userId, target, type);
    }

    @Override
    public Optional<LikeModel> find(Long userId, Long target, LikeType type) {
        return likeJpaRepository.findByUserIdAndTargetIdAndLikeType(userId, target, type);
    }

    @Override
    public Optional<LikeModel> findWithLock(Long userId, Long target, LikeType type) {
        return likeJpaRepository.findWithLockByUserIdAndTargetIdAndLikeType(userId, target, type);
    }
}
