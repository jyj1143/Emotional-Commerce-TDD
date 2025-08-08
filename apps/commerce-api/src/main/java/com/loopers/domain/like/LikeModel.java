package com.loopers.domain.like;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "likes",
    uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ref_user_id", "ref_target_id", "like_type"})
})
public class LikeModel extends BaseEntity {
    @Column(name = "ref_user_id", nullable = false)
    private Long userId;

    @Column(name = "ref_target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "like_type", nullable = false)
    private LikeType likeType;

    private LikeModel(Long userId, Long targetId, LikeType likeType) {
        if(userId == null ) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자는 비어있을 수 없습니다.");
        }
        if(targetId == null ) {
            throw new CoreException(ErrorType.NOT_FOUND, "대상자는 비어있을 수 없습니다.");
        }
        if (likeType == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "좋아요 타입은 필수 값입니다.");
        }
        this.userId = userId;
        this.targetId = targetId;
        this.likeType = likeType;
    }

    public static LikeModel of(Long userId, Long targetId, LikeType likeType) {
        return new LikeModel(userId, targetId, likeType);
    }
}
