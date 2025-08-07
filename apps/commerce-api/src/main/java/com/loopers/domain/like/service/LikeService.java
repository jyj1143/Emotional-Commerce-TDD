package com.loopers.domain.like.service;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.dto.LikeCommand;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = DataIntegrityViolationException.class)
    public void like(LikeCommand.Like command) {

        // 이미 존재하는지 확인
        if (likeRepository.isExists(command.userId(), command.targetId(), command.likeType())) {
            return; // 이미 존재하면 아무 작업 없이 리턴
        }

        try {
            LikeModel like = LikeModel.of(command.userId(), command.targetId(), command.likeType());
            likeRepository.save(like);
        } catch (DataIntegrityViolationException e) {
            // 동시성 문제로 인한 중복 등록 시도 - 무시
            // 이 예외는 롤백되지 않음 (noRollbackFor 설정)
           return;
        }

    }

    @Transactional
    public void unlike(LikeCommand.Unlike command) {
        likeRepository.deleteWithLock(command.userId(), command.targetId(), command.likeType());
    }

    public Long count(Long targetId, LikeType likeType) {
        return likeRepository.count(targetId, likeType);
    }
}
