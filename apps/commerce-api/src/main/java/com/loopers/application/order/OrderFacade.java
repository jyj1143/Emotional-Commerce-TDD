package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.coupone.dto.CouponCommand.Apply;
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

    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {

        // 상품 SKU 검증
        List<ProductSkuInfo> productSkuInfos = productSkuService.getValidSku(criteria.toProductCommand());

        // 주문 생성
        OrderInfo orderInfo = orderService.placeOrder(criteria.toOrderCommand(productSkuInfos));

        // 쿠폰 적용 및 최종 결제금액
        CouponDisCountInfo couponDisCountInfo = couponService.apply(
            new Apply(criteria.couponId(), orderInfo.id(), criteria.userId(), orderInfo.totalPrice())
        );

        // 결제 준비
        paymentService.ready(new PaymentCommand.Pay(criteria.userId(), orderInfo.id(), criteria.paymentMethod(), couponDisCountInfo.discountApplyPrice()));

        return OrderResult.from(orderInfo);
    }

}
