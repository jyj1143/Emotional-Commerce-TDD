package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.enums.LikeType;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeJpaRepository extends JpaRepository<LikeModel, Long> {

    Optional<LikeModel> findByUserIdAndTargetIdAndLikeType(Long userId, Long target, LikeType type);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM LikeModel l WHERE l.userId = :userId AND l.targetId = :targetId AND l.likeType = :likeType")
    Optional<LikeModel> findWithLockByUserIdAndTargetIdAndLikeType(
        @Param("userId") Long userId,
        @Param("targetId") Long targetId,
        @Param("likeType") LikeType likeType
    );

    Long countByTargetIdAndLikeType(Long target, LikeType type);

    Boolean existsByUserIdAndTargetIdAndLikeType(Long userId, Long target, LikeType type);
}
