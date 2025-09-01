package com.loopers.application.payment;

import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.repository.PaymentRepository;
import com.loopers.domain.payment.service.PaymentService;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class PaymentFacadeSyncTest  {

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

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

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

    @DisplayName("결제 트랜잭션 상태 테스트")
    @Nested
    class PaymentTransactionTest {

        @Test
        @DisplayName("PG 결제 처리 후 트랜잭션이 올바른 상태로 저장된다")
        void processPayment_TransactionStatusIsCorrect() {
            // Given
            PaymentCriteria.PgPay paymentCriteria = new PaymentCriteria.PgPay(
                    orderResult.id(),
                    CardType.SAMSUNG,
                    "1234-5678-1234-5678",
                    orderResult.totalPrice()
            );

            // Mock Payment Gateway Response - 성공
            PaymentGatewayInfo.Transaction mockResponse = new PaymentGatewayInfo.Transaction(
                    "TXN_SUCCESS_" + System.currentTimeMillis(),
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

            // DB에서 트랜잭션 상태 확인
            PaymentGatewayTransactionModel transaction = paymentRepository
                    .findTrxByOrderId(orderResult.id())
                    .orElseThrow();

            assertThat(transaction.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        }

        @Test
        @DisplayName("PG 결제 실패시 트랜잭션이 PENDING 상태로 남는다")
        void processPayment_WhenFailed_TransactionRemainesPending() {
            // Given
            PaymentCriteria.PgPay paymentCriteria = new PaymentCriteria.PgPay(
                    orderResult.id(),
                    CardType.SAMSUNG,
                    "1234-5678-1234-5678",
                    orderResult.totalPrice()
            );

            // Mock Payment Gateway Response - 실패
            PaymentGatewayInfo.Transaction mockResponse = new PaymentGatewayInfo.Transaction(
                    "TXN_FAILED_" + System.currentTimeMillis(),
                    PaymentStatus.FAILED,
                    "카드 한도 초과"
            );

            given(paymentGatewayAdapter.processPayment(any())).willReturn(mockResponse);

            // When
            TransactionResult result = paymentFacade.processPayment(paymentCriteria);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.status()).isEqualTo(PaymentStatus.FAILED);

            // DB에서 트랜잭션 상태 확인 - 실패한 경우 완료 처리되지 않으므로 PENDING 상태
            PaymentGatewayTransactionModel transaction = paymentRepository
                    .findTrxByOrderId(orderResult.id())
                    .orElseThrow();

            assertThat(transaction.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("PENDING 상태의 트랜잭션들을 조회할 수 있다")
        void findPendingTransactions() {
            // Given
            PaymentInfo paymentInfo = paymentService.findByRefOrderId(orderResult.id());

            // 여러 개의 PENDING 트랜잭션 생성
            String transactionKey1 = "TXN_PENDING_1_" + System.currentTimeMillis();
            String transactionKey2 = "TXN_PENDING_2_" + System.currentTimeMillis();

            paymentService.readyPaymentGatewayTransaction(
                    new PaymentCommand.ReadyTransaction(
                            paymentInfo.id(),
                            paymentInfo.orderId(),
                            transactionKey1,
                            PaymentStatus.PENDING,
                            paymentInfo.amount(),
                            CardType.SAMSUNG,
                            "1234-5678-1234-5678"
                    )
            );

            paymentService.readyPaymentGatewayTransaction(
                    new PaymentCommand.ReadyTransaction(
                            paymentInfo.id(),
                            paymentInfo.orderId(),
                            transactionKey2,
                            PaymentStatus.PENDING,
                            paymentInfo.amount(),
                            CardType.SAMSUNG,
                            "1234-5678-1234-5678"
                    )
            );

            // When
            List<PaymentGatewayTransactionModel> pendingTransactions =
                    paymentRepository.findPendingTransactions();

            // Then
            assertThat(pendingTransactions).hasSizeGreaterThanOrEqualTo(2);
            assertThat(pendingTransactions)
                    .extracting(PaymentGatewayTransactionModel::getPaymentStatus)
                    .containsOnly(PaymentStatus.PENDING);
        }


        @Test
        @DisplayName("트랜잭션 키로 특정 트랜잭션을 조회할 수 있다")
        void findTransactionByKey() {
            // Given
            PaymentInfo paymentInfo = paymentService.findByRefOrderId(orderResult.id());
            String transactionKey = "TXN_SEARCH_" + System.currentTimeMillis();

            PaymentGatewayTransactionModel savedTransaction = paymentService.readyPaymentGatewayTransaction(
                    new PaymentCommand.ReadyTransaction(
                            paymentInfo.id(),
                            paymentInfo.orderId(),
                            transactionKey,
                            PaymentStatus.PENDING,
                            paymentInfo.amount(),
                            CardType.SAMSUNG,
                            "1234-5678-1234-5678"
                    )
            );

            // When
            PaymentGatewayTransactionModel foundTransaction = paymentRepository
                    .findTransactionByKey(transactionKey)
                    .orElseThrow();

            // Then
            assertThat(foundTransaction.getTransactionKey()).isEqualTo(transactionKey);
            assertThat(foundTransaction.getRefOrderId()).isEqualTo(orderResult.id());
            assertThat(foundTransaction.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        }

        @Test
        @DisplayName("존재하지 않는 트랜잭션 키로 조회시 Optional.empty()를 반환한다")
        void findTransactionByKey_NotFound() {
            // Given
            String nonExistentTransactionKey = "NON_EXISTENT_KEY";

            // When
            Optional<PaymentGatewayTransactionModel> foundTransaction =
                    paymentRepository.findTransactionByKey(nonExistentTransactionKey);

            // Then
            assertThat(foundTransaction).isEmpty();
        }

        @Test
        @DisplayName("트랜잭션 상태를 수동으로 업데이트할 수 있다")
        void updateTransactionStatus() {
            // Given
            PaymentInfo paymentInfo = paymentService.findByRefOrderId(orderResult.id());
            String transactionKey = "TXN_UPDATE_" + System.currentTimeMillis();

            PaymentGatewayTransactionModel transaction = paymentService.readyPaymentGatewayTransaction(
                    new PaymentCommand.ReadyTransaction(
                            paymentInfo.id(),
                            paymentInfo.orderId(),
                            transactionKey,
                            PaymentStatus.PENDING,
                            paymentInfo.amount(),
                            CardType.SAMSUNG,
                            "1234-5678-1234-5678"
                    )
            );

            // When
            transaction.updateStatus(PaymentStatus.FAILED);
            PaymentGatewayTransactionModel savedTransaction = paymentRepository.save(transaction);

            // Then
            assertThat(savedTransaction.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

            // DB에서 다시 조회하여 확인
            PaymentGatewayTransactionModel reloadedTransaction = paymentRepository
                    .findTransactionByKey(transactionKey)
                    .orElseThrow();

            assertThat(reloadedTransaction.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        @DisplayName("트랜잭션을 완료 상태로 변경할 수 있다")
        void completeTransaction() {
            // Given
            PaymentInfo paymentInfo = paymentService.findByRefOrderId(orderResult.id());
            String transactionKey = "TXN_COMPLETE_" + System.currentTimeMillis();

            PaymentGatewayTransactionModel transaction = paymentService.readyPaymentGatewayTransaction(
                    new PaymentCommand.ReadyTransaction(
                            paymentInfo.id(),
                            paymentInfo.orderId(),
                            transactionKey,
                            PaymentStatus.PENDING,
                            paymentInfo.amount(),
                            CardType.SAMSUNG,
                            "1234-5678-1234-5678"
                    )
            );

            // When
            transaction.complete();
            PaymentGatewayTransactionModel savedTransaction = paymentRepository.save(transaction);

            // Then
            assertThat(savedTransaction.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(savedTransaction.getPaymentDate()).isNotNull();
        }
    }

}
