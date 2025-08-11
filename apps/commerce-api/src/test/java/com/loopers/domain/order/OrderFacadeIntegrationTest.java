package com.loopers.domain.order;


import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.dto.CouponInfo;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.DiscountType;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.domain.coupone.repository.CouponRepository;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.repository.ProductSkuRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.enums.CouponStatus;
import com.loopers.domain.point.service.dto.PointCommand;
import com.loopers.support.error.CoreException;
import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
public class OrderFacadeIntegrationTest {

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
    private PointService pointService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private static final Long USER_ID = 1L;
    private static final Long INITIAL_POINT = 50000L;
    private static final Long PRODUCT_PRICE = 10000L;
    private static final Long INITIAL_INVENTORY = 10L;

    private ProductSkuModel productSku;
    private CouponInfo coupon;
    private Long couponId;
    private InventoryModel inventory;
    private PointModel pointModel;

    @BeforeEach
    void setUp() {
        // 상품 생성
        productSku = ProductSkuModel.of(PRODUCT_PRICE, 0L, "색상", "RED", SaleStatus.ON_SALE, 1L);
        productSku = productSkuRepository.save(productSku);

        // 재고 생성
        inventory = InventoryModel.of(INITIAL_INVENTORY, productSku.getId());
        inventoryRepository.save(inventory);

        // 포인트 생성
        pointModel = PointModel.of(INITIAL_POINT, USER_ID);
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

    @DisplayName("주문을 생성할 때, ")
    @Nested
    class OrderPlace {

        @DisplayName("주문 성공 시, 모든 처리는 정상 반영되어야 한다")
        @Test
        void orderSuccess_shouldUpdateAllResources() {
            // Given
            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), 1L)),
                couponId
            );

            // When
            OrderResult result = orderFacade.order(orderCriteria);

            // Then
            // 주문이 성공했는지 확인
            assertNotNull(result);
            assertNotNull(result.id());
            assertEquals(USER_ID, result.userId());

            // 재고가 차감되었는지 확인
            InventoryModel updatedInventory = inventoryRepository.find(productSku.getId())
                .orElseThrow(() -> new RuntimeException("재고를 찾을 수 없습니다"));
            assertEquals(INITIAL_INVENTORY - 1, updatedInventory.getQuantity().getQuantity());

            // 포인트가 차감되었는지 확인
            PointModel updatedPoint = pointRepository.findByUserId(USER_ID)
                .orElseThrow(() -> new RuntimeException("포인트를 찾을 수 없습니다"));

            // 할인 금액 5000원을 적용하여 5000원만 차감되었는지 확인
            assertEquals(INITIAL_POINT - (PRODUCT_PRICE - 5000), updatedPoint.getAmount().getAmount());

            // 쿠폰이 사용되었는지 확인
            CouponModel usedCoupon = couponRepository.find(couponId).get();
            assertEquals(CouponStatus.USED, usedCoupon.getCouponStatus());
            assertEquals(result.id(), usedCoupon.getOrderId());
        }

        @DisplayName("사용 불가능한 쿠폰일 경우 주문은 실패해야 한다")
        @Test
        void orderWithInvalidCoupon_shouldFail() {
            // Given
            // 이미 사용된 쿠폰으로 만들기
            couponService.useCoupon(new CouponCommand.UseCoupon(couponId, 999L, USER_ID));

            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), 1L)),
                couponId
            );

            // When & Then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.order(orderCriteria);
            });
            assertTrue(exception.getMessage().contains("사용할 수 없는 쿠폰입니다"));

            // 재고와 포인트가 차감되지 않았는지 확인
            InventoryModel unchangedInventory = inventoryRepository.find(productSku.getId()).orElseThrow();
            assertEquals(INITIAL_INVENTORY, unchangedInventory.getQuantity().getQuantity());

            PointModel unchangedPoint = pointRepository.findByUserId(USER_ID).orElseThrow();
            assertEquals(INITIAL_POINT, unchangedPoint.getAmount().getAmount());
        }

        @DisplayName("재고가 부족할 경우 주문은 실패해야 한다")
        @Test
        void orderWithInsufficientInventory_shouldFail() {
            // Given
            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), INITIAL_INVENTORY + 1)),
                null
            );

            // When & Then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.order(orderCriteria);
            });
            assertTrue(exception.getMessage().contains("재고가 부족합니다"));

            // 포인트가 차감되지 않았는지 확인
            PointModel unchangedPoint = pointRepository.findByUserId(USER_ID).orElseThrow();
            assertEquals(INITIAL_POINT, unchangedPoint.getAmount().getAmount());

            // 쿠폰이 사용되지 않았는지 확인
            CouponModel unusedCoupon = couponRepository.find(couponId).orElseThrow();
            assertEquals(CouponStatus.AVAILABLE, unusedCoupon.getCouponStatus());
        }

        @DisplayName("포인트가 부족할 경우 주문은 실패해야 한다")
        @Test
        void orderWithInsufficientPoint_shouldFail() {
            // Given
            pointService.usePoint(new PointCommand.UsePoint(USER_ID, INITIAL_POINT - 100));

            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), 1L)),
                null
            );

            // When & Then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.order(orderCriteria);
            });
            assertTrue(exception.getMessage().contains("금액이 부족 합니다."));


            // 재고가 차감되지 않았는지 확인
            InventoryModel unchangedInventory = inventoryRepository.find(productSku.getId()).orElseThrow();
            assertEquals(INITIAL_INVENTORY, unchangedInventory.getQuantity().getQuantity());

            // 쿠폰이 사용되지 않았는지 확인
            CouponModel unusedCoupon = couponRepository.find(couponId).orElseThrow();
            assertEquals(CouponStatus.AVAILABLE, unusedCoupon.getCouponStatus());
        }

        @DisplayName("존재하지 않는 쿠폰일 경우 주문은 실패해야 한다")
        @Test
        void orderWithNonExistentCoupon_shouldFail() {
            // Given
            Long nonExistentCouponId = 99999L;

            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), 1L)),
                nonExistentCouponId
            );

            // When & Then
            CoreException exception = assertThrows(CoreException.class, () -> {
                orderFacade.order(orderCriteria);
            });
            assertTrue(exception.getMessage().contains("쿠폰을 찾을 수 없습니다"));

            // 재고와 포인트가 차감되지 않았는지 확인
            InventoryModel unchangedInventory = inventoryRepository.find(productSku.getId()).orElseThrow();
            assertEquals(INITIAL_INVENTORY, unchangedInventory.getQuantity().getQuantity());

            PointModel unchangedPoint = pointRepository.findByUserId(USER_ID).orElseThrow();
            assertEquals(INITIAL_POINT, unchangedPoint.getAmount().getAmount());
        }

        @DisplayName("존재하지 않는 상품으로 주문 시 실패해야 한다")
        @Test
        void orderWithNonExistentProduct_shouldFail() {
            // Given
            Long nonExistentProductId = 99999L;

            OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(nonExistentProductId, 1L)),
                null
            );

            // When & Then
            Exception exception = assertThrows(Exception.class, () -> {
                orderFacade.order(orderCriteria);
            });

            // 포인트가 차감되지 않았는지 확인
            PointModel unchangedPoint = pointRepository.findByUserId(USER_ID).orElseThrow();
            assertEquals(INITIAL_POINT, unchangedPoint.getAmount().getAmount());

            // 쿠폰이 사용되지 않았는지 확인
            CouponModel unusedCoupon = couponRepository.find(couponId).orElseThrow();
            assertEquals(CouponStatus.AVAILABLE, unusedCoupon.getCouponStatus());
        }



//        @DisplayName("정상적인 주문 생성 요청을 하면 주문 생성후 재고 차감 성공한다.")
//        @Test
//        void when_validOrderRequestGiven_then_createOrder() {
//            Long orderQuantity = 10L;
//            Long inventoryQuantity = 100L;
//
//            // given
//            ProductSkuModel productSkuModel = ProductSkuModel.of(0L,0L, "색상", "RED"
//                , SaleStatus.ON_SALE, 1L);
//            ProductSkuModel savedProductSkuModel = productSkuRepository.save(productSkuModel);
//
//            InventoryModel savedInventoryModel = inventoryRepository.save(InventoryModel.of(inventoryQuantity, savedProductSkuModel.getId()));
//            PointModel pointModel = PointModel.of(1000L, 1L);
//            pointRepository.save(pointModel);
//            pointRepository.increase(1L, 50000L);
//
//            Order order = new OrderCriteria.Order(
//                1L,
//                List.of(new OrderItem(savedProductSkuModel.getId(), 10L)),
//                null
//            );
//            OrderResult saved = orderFacade.order(order);
//            // when
//            Long currentQuantity= inventoryRepository.find(savedProductSkuModel.getId()).get().getQuantity().getQuantity();
//            // then
//            assertAll(
//                () -> assertThat(saved.id()).isNotNull(),
//                () -> assertThat(saved.userId()).isEqualTo(pointModel.getRefUserId()),
//                () -> assertThat(currentQuantity).isEqualTo(inventoryQuantity - orderQuantity)
//            );
//        }
    }
}
