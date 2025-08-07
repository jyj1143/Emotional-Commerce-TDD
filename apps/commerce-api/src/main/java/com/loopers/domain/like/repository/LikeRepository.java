package com.loopers.domain.like.repository;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.enums.LikeType;
import java.util.Optional;

public interface LikeRepository {

    LikeModel save(LikeModel like);

    Long count(Long target, LikeType type);

    boolean isExists(Long userId, Long target, LikeType type);

    Optional<LikeModel> find(Long userId, Long target, LikeType type);

    void deleteWithLock(Long userId, Long targetId, LikeType likeType);

    Optional<LikeModel> findWithLock(Long userId, Long target, LikeType type);
}
