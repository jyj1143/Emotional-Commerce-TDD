package com.loopers.interfaces.event.coupon;

import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventListener {
    private final CouponService couponService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCreatedEvent(OrderEvent.Created event) {
        // 쿠폰 적용
        if (event.couponId() != null) {
            couponService.useCoupon(new CouponCommand.UseCoupon(
                    event.couponId(),
                    event.orderId(),
                    event.userId()
                ));
        }
    }

    /**
     * 주문 취소 이벤트 처리
     * - 쿠폰 복원
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleOrderCancelledEvent(OrderEvent.Cancelled event) {
        if (event.couponId() != null) {
            couponService.restoreCoupon(new CouponCommand.RestoreCoupon(
                event.couponId(),
                event.userId()
            ));
        }

        log.info("주문 취소로 인한 쿠폰 복원 완료 - 주문 ID: {}", event.orderId());
    }
}
