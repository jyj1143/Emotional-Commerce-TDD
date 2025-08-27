package com.loopers.interfaces.event.product;

import com.loopers.domain.like.dto.LikeEvent;
import com.loopers.domain.product.dto.product.ProductEvent;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.product.service.ProductSignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductSignalEventListener {

    private final ProductSignalService productSignalService;

    /**
     * 상품 등록 이벤트 처리
     * - 상품 등록 시, 상품 시그널 테이블 생성
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRegisterProductSignal(ProductEvent.Register event) {
        productSignalService.register(event.productId());
    }

    /**
     * 좋아요 이벤트 처리
     * - 상품 좋아요 시, 상품 신호의 좋아요 수 증가
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleIncreaseLikeCount(LikeEvent.Like event) {
        if (LikeType.PRODUCT.equals(event.likeType())) {
            productSignalService.increaseLikeCount(event.targetId());
        }
    }

    /**
     * 좋아요 취소 이벤트 처리
     * - 상품 좋아요 취소 시, 상품 신호의 좋아요 수 감소
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDecreaseLikeCount(LikeEvent.UnLike event) {
        if (LikeType.PRODUCT.equals(event.likeType())) {
            productSignalService.decreaseLikeCount(event.targetId());
        }
    }

}
