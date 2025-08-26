package com.loopers.application.payment;

import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.dto.OrderCriteria;
import com.loopers.application.order.dto.OrderResult;
import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.TransactionResult;
import com.loopers.application.payment.strategy.CardPaymentStrategy;
import com.loopers.application.payment.strategy.PaymentContext;
import com.loopers.application.payment.strategy.PointPaymentStrategy;
import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.dto.CouponInfo;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.DiscountType;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.domain.coupone.service.CouponService;
import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.payment.adapter.PaymentGatewayAdapter;
import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.repository.ProductSkuRepository;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PaymentFacadeTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductSkuRepository productSkuRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @MockitoBean
    private PaymentGatewayAdapter paymentGatewayAdapter;

    @Autowired
    private PaymentContext paymentContext;

    @Autowired
    private CardPaymentStrategy cardPaymentStrategy;

    @Autowired
    private PointPaymentStrategy pointPaymentStrategy;

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_PRICE = 10000L;
    private static final Long INITIAL_INVENTORY = 100L;
    private static final Long INITIAL_POINT = 50000L;

    private ProductSkuModel productSku;
    private InventoryModel inventory;
    private PointModel pointModel;
    private CouponPolicyModel couponPolicy;
    private CouponInfo coupon;
    private Long couponId;
    private OrderResult orderResult;

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
                10L,
                0,
                5000,
                new BigDecimal("5000"),
                10L // 초기 10개
        );
        this.couponPolicy = couponPolicyRepository.save(couponPolicy);

        // 쿠폰 발급
        coupon = couponService.issueCoupon(new CouponCommand.IssueCoupon(this.couponPolicy.getId(), USER_ID));
        couponId = coupon.id();

        // 주문 생성
        OrderCriteria.Order orderCriteria = new OrderCriteria.Order(
                USER_ID,
                List.of(new OrderCriteria.Order.OrderItem(productSku.getId(), 2L)),
                couponId,
                PaymentMethod.CARD
        );

        orderResult = orderFacade.order(orderCriteria);

        // 결제 전략 초기화
        paymentContext.registerStrategy(PaymentMethod.CARD, cardPaymentStrategy);
        paymentContext.registerStrategy(PaymentMethod.POINT, pointPaymentStrategy);
    }

    @Test
    @DisplayName("카드 결제 처리가 성공적으로 완료된다")
    void processCardPayment_Success() {
        // Given
        PaymentCriteria.PgPay paymentCriteria = new PaymentCriteria.PgPay(
                orderResult.id(),
                CardType.SAMSUNG,
                "1234-5678-1234-5678",
                orderResult.totalPrice()
        );

        // Mock Payment Gateway Response - 성공
        PaymentGatewayInfo.Transaction mockResponse = new PaymentGatewayInfo.Transaction(
                "TXN_1",
                PaymentStatus.COMPLETED,
                "결제 성공"
        );

        given(paymentGatewayAdapter.processPayment(any())).willReturn(mockResponse);

        // When
        TransactionResult result = paymentFacade.processPayment(paymentCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.transactionKey()).isEqualTo(mockResponse.transactionKey());
        assertThat(result.status()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제 게이트웨이 오류시 실패한다")
    void processPayment_GatewayError() {
        // Given
        PaymentCriteria.PgPay paymentCriteria = new PaymentCriteria.PgPay(
                orderResult.id(),
                CardType.SAMSUNG,
                "1234-5678-1234-5678",
                orderResult.totalPrice()
        );

        // Mock Payment Gateway Response - 실패
        PaymentGatewayInfo.Transaction mockResponse = new PaymentGatewayInfo.Transaction(
                "TXN_1",
                PaymentStatus.FAILED,
                "카드 한도 초과"
        );

        given(paymentGatewayAdapter.processPayment(any())).willReturn(mockResponse);

        // When
        TransactionResult result = paymentFacade.processPayment(paymentCriteria);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(PaymentStatus.FAILED);
        assertThat(result.reason()).isEqualTo("카드 한도 초과");
    }

    @Test
    @DisplayName("존재하지 않는 주문ID로 결제 요청시 예외가 발생한다")
    void processPayment_OrderNotFound() {
        // Given
        Long nonExistentOrderId = 99999L;
        PaymentCriteria.PgPay paymentCriteria = new PaymentCriteria.PgPay(
                nonExistentOrderId,
                CardType.SAMSUNG,
                "1234-5678-1234-5678",
                10000L
        );

        // When & Then
        assertThatThrownBy(() -> paymentFacade.processPayment(paymentCriteria))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다");
    }

}
