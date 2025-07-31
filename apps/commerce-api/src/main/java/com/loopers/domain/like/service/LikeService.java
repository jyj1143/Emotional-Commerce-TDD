package com.loopers.domain.like.service;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.dto.LikeCommand;
import com.loopers.domain.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional
    public void like(LikeCommand.Like command) {
        LikeModel like = LikeModel.of(command.userId(), command.targetId(), command.likeType());
        if (likeRepository.isExists(like.getUserId(), like.getTargetId(), like.getLikeType())) {
            return;
        }
        likeRepository.save(like);
    }

    @Transactional
    public void unlike(final LikeCommand.Unlike command) {
        likeRepository.find(command.userId(), command.targetId(), command.likeType())
            .ifPresent(likeRepository::delete);
    }
}
