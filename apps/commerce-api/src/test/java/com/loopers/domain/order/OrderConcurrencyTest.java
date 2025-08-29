package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.dto.CouponInfo;
import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.CouponStatus;
import com.loopers.domain.coupone.enums.DiscountType;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.domain.coupone.repository.CouponRepository;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.inventory.service.InventoryService;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.repository.OrderRepository;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.repository.PaymentRepository;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand;
import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.repository.ProductSkuRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductSkuRepository productSkuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private static final Long USER_ID = 1L;
    private static final Long INITIAL_POINT = 1000000L;
    private static final Long PRODUCT_PRICE = 10000L;
    private static final Long INITIAL_INVENTORY = 100L;

    private ProductSkuModel productSku;
    private CouponInfo coupon;
    private Long couponId;

    @BeforeEach
    void setUp() {
        // 상품 생성
        productSku = ProductSkuModel.of(PRODUCT_PRICE, 0L, "색상", "RED", SaleStatus.ON_SALE, 1L);
        productSku = productSkuRepository.save(productSku);

        // 재고 생성
        InventoryModel inventory = InventoryModel.of(INITIAL_INVENTORY, productSku.getId());
        inventoryRepository.save(inventory);

        // 포인트 생성
        PointModel pointModel = PointModel.of(INITIAL_POINT, USER_ID);
        pointRepository.save(pointModel);

        // 쿠폰 정책 생성
        CouponPolicyModel couponPolicy = CouponPolicyModel.of(
            "테스트 쿠폰",
            "테스트용 할인 쿠폰",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(30),
            DiscountType.FIXED_AMOUNT,
            10L, // 총 10개의 쿠폰
            0,
            5000, // 최대 5000원 할인
            new BigDecimal("5000"), // 5000원 할인
            10L // 초기 10개
        );
        couponPolicy = couponPolicyRepository.save(couponPolicy);

        // 쿠폰 발급
        coupon = couponService.issueCoupon(new CouponCommand.IssueCoupon(couponPolicy.getId(), USER_ID));
        couponId = coupon.id();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("동일한 쿠폰으로 여러 기기에서 동시에 주문해도, 쿠폰은 단 한번만 사용되어야 한다")
    @Test
    void concurrentOrdersWithSameCoupon() throws Exception {
        // Given
        final int numOfOrders = 5; // 동시에 5개 주문 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numOfOrders);
        CountDownLatch latch = new CountDownLatch(numOfOrders);
        List<Future<OrderResult>> futures = new ArrayList<>();

        // When
        for (int i = 0; i < numOfOrders; i++) {
            Future<OrderResult> future = executorService.submit(() -> {
                try {
                    OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                        USER_ID,
                        List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), 1L)),
                        couponId,
                        PaymentMethod.POINT
                    );
                    return orderFacade.order(orderCriteria);
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();

        // Then
        // 성공한 주문 확인
        List<OrderResult> successfulOrders = new ArrayList<>();
        for (Future<OrderResult> future : futures) {
            try {
                OrderResult result = future.get();
                if (result != null) {
                    successfulOrders.add(result);
                }
            } catch (Exception e) {
                // 예외가 발생한 요청은 무시
            }
        }

        // 쿠폰 상태 확인
        CouponModel usedCoupon = couponRepository.find(couponId)
            .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다"));

        // 쿠폰이 정확히 한 번만 사용되었는지 확인
        assertAll(
            () -> assertThat(usedCoupon.getCouponStatus()).isEqualTo(CouponStatus.USED),
            () -> assertThat(usedCoupon.getOrderId()).isNotNull(),
            () -> assertThat(successfulOrders.size()).isGreaterThanOrEqualTo(1),
            // 성공한 주문 중 하나만 쿠폰을 사용했는지 확인
            () -> assertThat(successfulOrders.stream()
                .filter(order -> order.id().equals(usedCoupon.getOrderId()))
                .count()).isEqualTo(1)
        );
    }


    @DisplayName("동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다")
    @Test
    void concurrentOrdersWithSameProduct() throws Exception {
        // Given
        final int numOfOrders = 50; // 동시에 50개 주문 시도
        final long quantityPerOrder = 2L; // 각 주문당 구매할 수량

        ExecutorService executorService = Executors.newFixedThreadPool(numOfOrders);
        CountDownLatch latch = new CountDownLatch(numOfOrders);
        List<Future<OrderResult>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < numOfOrders; i++) {
            Future<OrderResult> future = executorService.submit(() -> {
                try {
                    OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                        USER_ID,
                        List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), quantityPerOrder)),
                        null,
                        PaymentMethod.POINT
                    );
                    OrderResult result = orderFacade.order(orderCriteria);
                    successCount.incrementAndGet();
                    return result;
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    return null;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기
        executorService.shutdown();

        // Then
        // 최종 재고 확인
        InventoryModel inventory = inventoryRepository.find(productSku.getId())
            .orElseThrow(() -> new RuntimeException("재고를 찾을 수 없습니다"));

        // 성공한 주문의 총 수량
        long totalOrderedQuantity = successCount.get() * quantityPerOrder;

        assertAll(
            () -> assertThat(successCount.get() + failCount.get()).isEqualTo(numOfOrders),
            () -> assertThat(inventory.getQuantity().getQuantity()).isEqualTo(INITIAL_INVENTORY - totalOrderedQuantity),
            // 재고 초과 주문이 있었다면 그 수량만큼 실패해야 함
            () -> {
                if (totalOrderedQuantity > INITIAL_INVENTORY) {
                    assertThat(failCount.get()).isGreaterThan(0);
                }
            }
        );
    }
}
