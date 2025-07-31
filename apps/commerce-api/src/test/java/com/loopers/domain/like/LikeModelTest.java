package com.loopers.domain.like;

import com.loopers.domain.like.enums.LikeType;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LikeModelTest {

    @DisplayName("좋아요 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("회원 ID가 비어있으면, CoreException 예외가 발생한다.")
        @Test
        void when_userIdNull_then_throwCoreException() {
            assertThrows(CoreException.class, () -> {
                LikeModel.of(null, 1L, LikeType.PRODUCT);
            });
        }

        @DisplayName("타겟 ID가 비어있으면, CoreException 예외가 발생한다.")
        @Test
        void when_targetIdNull_then_throwCoreException() {
            assertThrows(CoreException.class, () -> {
                LikeModel.of(1L, null, LikeType.PRODUCT);
            });
        }

        @DisplayName("좋아요 타입이 비어있으면, CoreException 예외가 발생한다.")
        @Test
        void when_targetTypeNull_then_throwCoreException() {
            assertThrows(CoreException.class, () -> {
                LikeModel.of(1L, 1L, null);
            });
        }
    }
}
