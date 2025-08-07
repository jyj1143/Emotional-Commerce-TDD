package com.loopers.application.order;

import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.inventory.service.InventoryService;
import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.service.OrderService;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.service.PaymentService;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand.UsePoint;
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
    private final InventoryService inventoryService;
    private final PointService pointService;
    private final ProductSkuService productSkuService;
    private final CouponService couponService;

    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {

        // 상품 SKU 검증
        List<ProductSkuInfo> productSkuInfos = productSkuService.getValidSku(criteria.toProductCommand());

        // 주문 생성
        OrderInfo orderInfo = orderService.placeOrder(criteria.toOrderCommand(productSkuInfos));

        // 쿠폰 적용 및 최종 결제금액
        Long finalPrice = getFinalPrice(criteria, orderInfo);

        // 포인트 사용
        pointService.usePoint(new UsePoint(criteria.userId(), finalPrice));

        // 재고 감소
        criteria.orderItems().forEach(item ->
            inventoryService.decrease(new InventoryCommand.DecreaseStock(
                item.skuId(),
                item.quantity()))
        );

        // 결제 처리
        paymentService.pay(new PaymentCommand.Pay(
            orderInfo.id(),
            PaymentMethod.POINT,
            finalPrice
        ));

        // 주문 완료
        OrderInfo successOrder = orderService.completeOrder(orderInfo.id());

        return OrderResult.from(successOrder);
    }

    private Long getFinalPrice(OrderCriteria.Order criteria, OrderInfo orderInfo) {
        Long totalPrice = orderInfo.totalPrice();
        Long finalPrice = totalPrice;

        // 쿠폰 적용
        if (criteria.couponId() != null) {
                Long discountAmount = couponService.calculateDiscount(criteria.couponId(), criteria.userId(), totalPrice);
                finalPrice = Math.max(0, totalPrice - discountAmount);

                // 쿠폰 사용 처리
                couponService.useCoupon(new CouponCommand.UseCoupon(
                    criteria.couponId(), orderInfo.id(), criteria.userId()));
        }
        return finalPrice;
    }

}
