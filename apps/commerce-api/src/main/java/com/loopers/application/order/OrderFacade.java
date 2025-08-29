package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.dto.CouponDisCountInfo;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.service.OrderService;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.service.PaymentService;
import com.loopers.domain.product.dto.sku.ProductSkuInfo;
import com.loopers.domain.product.service.ProductSkuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductSkuService productSkuService;
    private final CouponService couponService;

    /**
     * 주문 프로세스 흐름 - 재고 선점 선차감 방식 적용:
     * 1. 주문 생성 단계: 데이터 유효성 검증, 쿠폰 사용 처리, 재고 선차감
     * 2. 결제 완료 단계: 포인트 적립/차감 및 기타 리소스 반영
     * 미결제 시 롤백 정책: 일정 시간 후 미결제된 주문의 재고 및 쿠폰은 원복처리
     */
    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {

        // 상품 SKU 검증
        List<ProductSkuInfo> productSkuInfos = productSkuService.getValidSku(criteria.toProductCommand());

        // 주문 생성
        OrderInfo orderInfo = orderService.placeOrder(criteria.toOrderCommand(productSkuInfos));

        // 쿠폰 최종 결제금액
        CouponDisCountInfo couponDisCountInfo = couponService.getTotalPrice(
            new CouponCommand.Calculate(criteria.couponId(), criteria.userId(), orderInfo.totalPrice())
        );

        orderService.pendingPayment(criteria.toOrderCommand(orderInfo.id(), couponDisCountInfo.discountApplyPrice()));

        return OrderResult.from(orderInfo);
    }

}
