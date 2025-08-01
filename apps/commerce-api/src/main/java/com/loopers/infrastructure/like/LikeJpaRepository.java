package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.enums.LikeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<LikeModel, Long> {

    Optional<LikeModel> findByUserIdAndTargetIdAndLikeType(Long userId, Long target, LikeType type);

    Long countByTargetIdAndLikeType(Long target, LikeType type);

    Boolean existsByUserIdAndTargetIdAndLikeType(Long userId, Long target, LikeType type);
}
