package com.loopers.domain.coupone.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.CouponStatus;
import com.loopers.domain.coupone.enums.DiscountType;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.domain.coupone.repository.CouponRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.support.pagenation.PageResult;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService sut;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("쿠폰 발급할 때, ")
    @Nested
    class IssueCoupon {

        @DisplayName("[fail] - 쿠폰 정책이 없을 때 예외 발생")
        @Test
        void given_noCouponPolicy_when_issueCoupon_then_throwNotFoundError() {
            // Given
            Long invalidPolicyId = -1L;
            Long userId = 1L;
            CouponCommand.IssueCoupon command = new CouponCommand.IssueCoupon(invalidPolicyId, userId);

            // When
            CoreException actual = assertThrows(CoreException.class, () -> sut.issueCoupon(command));

            // Then
            assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "쿠폰 정책을 찾을 수 없습니다."));
        }

        @DisplayName("[fail] - 쿠폰 발급 기간 외에 발급시도하면 예외 발생")
        @Test
        void given_outOfPeriod_when_issueCoupon_then_throwBadRequestError() {
            // Given
            CouponPolicyModel couponPolicyModel = CouponPolicyModel.of(
                "만료된 쿠폰",
                "쿠폰 설명",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5),
                DiscountType.FIXED_AMOUNT,
                100L,
                0,
                0,
                new BigDecimal("100"),
                100L
            );
            CouponPolicyModel savedPolicyModel = couponPolicyRepository.save(couponPolicyModel);
            Long policyId = savedPolicyModel.getId();
            Long userId = 1L;
            CouponCommand.IssueCoupon command = new CouponCommand.IssueCoupon(policyId, userId);

            // When
            CoreException actual = assertThrows(CoreException.class, () -> sut.issueCoupon(command));

            // Then
            assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "쿠폰 발급 기간이 아닙니다."));
        }

        @DisplayName("[fail] - 쿠폰이 모두 소진되면 예외 발생")
        @Test
        void given_noRemainingCoupons_when_issueCoupon_then_throwNotFoundError() {
            // Given
            CouponPolicyModel couponPolicyModel = CouponPolicyModel.of(
                "소진된 쿠폰",
                "쿠폰 설명",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5),
                DiscountType.FIXED_AMOUNT,
                0L, // 잔여 수량 0
                0,
                0,
                new BigDecimal("100"),
                0L
            );
            CouponPolicyModel savedPolicyModel = couponPolicyRepository.save(couponPolicyModel);
            Long policyId = savedPolicyModel.getId();
            Long userId = 1L;
            CouponCommand.IssueCoupon command = new CouponCommand.IssueCoupon(policyId, userId);

            // When
            CoreException actual = assertThrows(CoreException.class, () -> sut.issueCoupon(command));

            // Then
            assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "쿠폰이 모두 소진되었습니다."));
        }

        @DisplayName("[happy] - 쿠폰 정책이 주어지면, 쿠폰을 발급한다.")
        @Test
        void given_couponPolicy_when_createCoupon_then_createCoupon() throws InterruptedException {

            Long testQuantity = 500L;

            CouponPolicyModel couponPolicyModel = CouponPolicyModel.of(
                "테스트 쿠폰",
                "테스트 쿠폰 설명",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.FIXED_AMOUNT,
                testQuantity, // 총 testQuantity개의 쿠폰만 발급 가능
                0,
                0,
                new BigDecimal("100"),
                testQuantity// 초기 잔여 수량 testQuantity개
            );

            CouponPolicyModel savedCouponPolicyModel = couponPolicyRepository.save(couponPolicyModel);
            Long couponPolicyId = savedCouponPolicyModel.getId();

            int numberOfThreads = (int) (testQuantity * 2);// testQuantity*2명의 사용자가 동시에 요청
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                final Long userId = (long) (i + 1);
                executorService.submit(() -> {
                    try {
                        CouponCommand.IssueCoupon command = new CouponCommand.IssueCoupon(couponPolicyId, userId);
                        sut.issueCoupon(command);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 모든 스레드가 작업을 마칠 때까지 대기
            latch.await();
            executorService.shutdown();

            // Then
            assertEquals(testQuantity, failCount.get()); // testQuantity 개의 요청은 실패해야 함
            assertEquals(testQuantity, successCount.get()); // testQuantity 개의 쿠폰만 성공적으로 발급되어야 함

            // 데이터베이스에서 실제 발급된 쿠폰 수 확인
            long actualIssuedCount = couponRepository.countByCouponPolicyId(couponPolicyId);
            assertEquals(testQuantity, actualIssuedCount);

            // 쿠폰 정책의 잔여 수량이 0인지 확인
            CouponPolicyModel updatedPolicy = couponPolicyRepository.find(couponPolicyId).orElseThrow();
            assertEquals(0L, updatedPolicy.getRemainQuantity().getQuantity());
        }

    }


    @DisplayName("쿠폰 사용할 때, ")
    @Nested
    class UseCoupon {

        private static final Long TEST_USER_ID = 1L;
        private static final Long TEST_COUPON_ID = 1L;
        private static final Long TEST_ORDER_ID = 1L;

        @DisplayName("[happy] - 정상적으로 쿠폰 발급후, 쿠폰사용 성공한다.")
        @Test
        void when_issueCoupon_then_useCoupon() {
            // Given
            CouponPolicyModel couponPolicyModel = CouponPolicyModel.of(
                "테스트 쿠폰",
                "테스트 쿠폰 설명",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(10),
                DiscountType.FIXED_AMOUNT,
                100L,
                0,
                0,
                new BigDecimal("100"),
                100L
            );
            CouponPolicyModel savedCouponPolicyModel = couponPolicyRepository.save(couponPolicyModel);
            Long couponPolicyId = savedCouponPolicyModel.getId();

            CouponCommand.IssueCoupon issueCoupon = new CouponCommand.IssueCoupon(couponPolicyId, TEST_USER_ID);
            sut.issueCoupon(issueCoupon);
            CouponCommand.UseCoupon useCoupon = new CouponCommand.UseCoupon(couponPolicyId, TEST_ORDER_ID, TEST_USER_ID);

            // When
            CouponModel actual = sut.useCoupon(useCoupon);

            // Then
            assertAll(
                () -> assertThat(actual.getId()).isEqualTo(couponPolicyId),
                () -> assertThat(actual.getOrderId()).isEqualTo(TEST_ORDER_ID),
                () -> assertThat(actual.getCouponStatus()).isEqualTo(CouponStatus.USED)
            )
            ;
        }

        @DisplayName("[fail] - 쿠폰이 존재하지 않으면 NOT FOUND 예외 발생")
        @Test
        void given_nonExistingCoupon_when_useCoupon_then_throwNotFoundError() {
            // Given
            // When
            CouponCommand.UseCoupon useCoupon = new CouponCommand.UseCoupon(TEST_COUPON_ID, TEST_ORDER_ID, TEST_USER_ID);
            // Then
            CoreException actual = assertThrows(CoreException.class, () -> {
                sut.useCoupon(useCoupon);
            });
            assertThat(actual).usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));
        }
    }

    @DisplayName("쿠폰 조회할 때, ")
    @Nested
    class GetCoupons {
        private static final Long TEST_USER_ID = 1000L;
        private static final int DEFAULT_PAGE = 1;
        private static final int DEFAULT_PAGE_SIZE = 10;

        @DisplayName("[happy] - 정상적으로 쿠폰 발급후, 회원 쿠폰조회 성공한다.")
        @Test
        void when_issueCoupons_then_SearchSuccess() {
            // Given
            CouponPolicyModel couponPolicy = createTestCouponPolicy();
            CouponPolicyModel savedCouponPolicy = couponPolicyRepository.save(couponPolicy);

            CouponCommand.IssueCoupon issueCouponCommand = new CouponCommand.IssueCoupon(
                savedCouponPolicy.getId(),
                TEST_USER_ID
            );
            CouponModel issuedCoupon = sut.issueCoupon(issueCouponCommand);

            // When
            CouponCommand.GetMyCoupons getMyCouponsCommand = new CouponCommand.GetMyCoupons(
                DEFAULT_PAGE,
                DEFAULT_PAGE_SIZE,
                TEST_USER_ID
            );
            PageResult<CouponModel> couponsResult = sut.getCoupons(getMyCouponsCommand);

            // Then
            assertAll(
                () -> assertThat(couponsResult.content()).hasSize(1),
                () -> assertThat(couponsResult.content().get(0).getId()).isEqualTo(issuedCoupon.getId()),
                () -> assertThat(couponsResult.content().get(0).getRefUserId()).isEqualTo(TEST_USER_ID)
            );
        }
    }

    private CouponPolicyModel createTestCouponPolicy() {
        return CouponPolicyModel.of(
            "테스트 쿠폰",
            "테스트 쿠폰 설명",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(10),
            DiscountType.FIXED_AMOUNT,
            100L,
            0,
            0,
            new BigDecimal("100"),
            100L
        );
    }


}
