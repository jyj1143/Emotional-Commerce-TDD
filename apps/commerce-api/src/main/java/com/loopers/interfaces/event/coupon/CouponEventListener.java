package com.loopers.interfaces.event.coupon;

import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
}
