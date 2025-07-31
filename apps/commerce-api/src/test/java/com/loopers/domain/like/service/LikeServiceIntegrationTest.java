package com.loopers.domain.like.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.dto.LikeCommand;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.repository.LikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LikeServiceIntegrationTest {

    @Autowired
    LikeService sut;
    @Autowired
    LikeRepository likeRepository;

    @DisplayName("좋아요를 등록할 때, ")
    @Nested
    class Like {
        @DisplayName("좋아요하지 않은 상태면, 좋아요 등록한다.")
        @Test
        void when_userHasNotLikedTarget_then_registerLike() {
            LikeCommand.Like command=new LikeCommand.Like(1L, 1L, LikeType.PRODUCT);
            boolean isExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            sut.like(command);

            assertThat(isExist).isFalse();
        }
    }

    @DisplayName("좋아요를 취소할 때, ")
    @Nested
    class UnLike {
        @DisplayName("좋아요한 상태면, 좋아요 취소한다.")
        @Test
        void when_userHasLikedTarget_then_registerLike() {
            LikeCommand.Unlike command = new LikeCommand.Unlike(1L, 1L, LikeType.PRODUCT);
            LikeModel likeModel = LikeModel.of(command.userId(), command.targetId(), command.likeType());
            likeRepository.save(likeModel);
            boolean isExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            sut.unlike(command);

            assertThat(isExist).isTrue();
        }
    }

}
