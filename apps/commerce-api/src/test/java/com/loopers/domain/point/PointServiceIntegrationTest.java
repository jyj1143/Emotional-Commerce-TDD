package com.loopers.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand;
import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("포인트 조회 시,")
    @Nested
    class GetPoint {

        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void whenExistUser_thenReturnUserInfo() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");
            Email email = new Email("test@gmail.com");
            Gender male = Gender.MALE;
            BirthDate birthDate = new BirthDate("1997-02-27");
            UserCommand.Create user = new UserCommand.Create(
                loginInfo,
                email,
                male,
                birthDate
            );
            userService.signUp(user);

            // when
            UserModel userModel = userService.getUser(loginInfo);
            userModel.addPoint(1000L);
            // then
            assertAll(
                () -> assertThat(userModel).isNotNull(),
                () -> assertThat(userModel.getLoginInfo().getLoginId()).isEqualTo(loginInfo.getLoginId()),
                () -> assertThat(userModel.getEmail().getEmail()).isEqualTo(email.getEmail()),
                () -> assertThat(userModel.getGender()).isEqualTo(male),
                () -> assertThat(userModel.getBirthDate().getBirthDate()).isEqualTo(birthDate.getBirthDate()),
                () -> assertThat(userModel.getPoint().getPoint()).isEqualTo(1000L)
            );
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        void whenNotExistUser_whenRequestPoint_thenReturnNull() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");

            // when
            UserModel user = userService.getUser(loginInfo);

            // then
            assertAll(
                () -> assertThat(user).isNull()
            );
        }
    }

    @DisplayName("포인트 충전 시,")
    @Nested
    class ChargePoint {

        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        void whenChargePointWithNonExistentUser_thenFail() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");
            Long chargeAmount = 1000L;

            // when
            CoreException coreException = assertThrows(CoreException.class,
                () -> userService.addPoint(loginInfo, chargeAmount));

            // then
            assertAll(
                () -> assertEquals(ErrorType.NOT_FOUND, coreException.getErrorType())
            );
        }
    }


    @DisplayName("동시에 포인트 사용 시,")
    @Nested
    class ConcurrencyUsePoint {

        @DisplayName("비관적 락으로 인해 모든 요청이 순차적으로 처리된다")
        @Test
        void when_multipleConcurrentPointUsage_then_allRequestsProcessedSequentially() throws InterruptedException {
            // Given
            Long userId = 1L;
            Long initialAmount = 10000L;
            PointModel pointModel = PointModel.of(initialAmount, userId);
            pointRepository.save(pointModel);

            Long useAmount = 1000L;
            int numberOfThreads = 5;

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(numberOfThreads);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger exceptionCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        readyLatch.countDown();
                        startLatch.await();

                        pointService.usePoint(new PointCommand.UsePoint(userId, useAmount));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        exceptionCount.incrementAndGet();
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            readyLatch.await();
            startLatch.countDown();
            completionLatch.await();
            executorService.shutdown();

            // Then
            PointModel updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            Long finalAmount = updatedPoint.getAmount().getAmount();

            assertAll(
                // 모든 요청이 성공해야 함
                () -> assertThat(successCount.get()).isEqualTo(numberOfThreads),
                // 예외는 발생하지 않아야 함
                () -> assertThat(exceptionCount.get()).isEqualTo(0),
                // 최종 포인트는 모든 요청이 처리된 금액
                () -> assertThat(finalAmount).isEqualTo(initialAmount - (numberOfThreads * useAmount))
            );
        }
    }


    @DisplayName("동시에 포인트 충전 시,")
    @Nested
    class ConcurrencyChargePoint {

        @DisplayName("비관적 락을 사용하여 모든 충전 요청이 순차적으로 처리된다")
        @Test
        void when_multipleConcurrentPointCharge_then_allRequestsProcessedSequentially() throws InterruptedException {
            // Given
            // 포인트 초기화
            Long userId = 1L;
            Long initialAmount = 0L;
            PointModel pointModel = PointModel.of(initialAmount, userId);
            pointRepository.save(pointModel);

            // 동시에 충전할 포인트 금액
            Long chargeAmount = 1000L;

            // 여러 스레드로 동시에 포인트 충전 요청
            int numberOfThreads = 5; // 동시 요청 수
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch readyLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 준비될 때까지 대기
            CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호를 위한 래치
            CountDownLatch completionLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 완료될 때까지 대기

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger exceptionCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        // 준비 완료 신호
                        readyLatch.countDown();
                        // 모든 스레드가 동시에 시작하도록 대기
                        startLatch.await();

                        // 포인트 충전 요청
                        pointService.chargePoint(new PointCommand.ChargePoint(userId, chargeAmount));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        exceptionCount.incrementAndGet();
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            // 모든 스레드가 준비될 때까지 대기
            readyLatch.await();
            // 모든 스레드에게 시작 신호
            startLatch.countDown();
            // 모든 스레드가 완료될 때까지 대기
            completionLatch.await();
            executorService.shutdown();

            // Then
            // 최종 포인트 조회
            PointModel updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            Long finalAmount = updatedPoint.getAmount().getAmount();

            assertAll(
                // 비관적 락을 사용하면 모든 요청이 성공해야 함
                () -> assertThat(successCount.get()).isEqualTo(numberOfThreads),
                // 예외는 발생하지 않아야 함
                () -> assertThat(exceptionCount.get()).isEqualTo(0),
                // 최종 포인트는 초기 금액에 모든 충전 금액이 더해져야 함
                () -> assertThat(finalAmount).isEqualTo(initialAmount + (chargeAmount * numberOfThreads))
            );
        }
    }


    @DisplayName("동시에 포인트 충전 과 사용 시,")
    @Nested
    class ConcurrencyChargeAndUsePoint {
        @DisplayName("한번만 성공하고 나머지 ObjectOptimisticLockingFailureException가 발생한다")
        @Test
        void when_concurrentChargeAndUsePoint_then_bothRequestsProcessedSequentially() throws InterruptedException {
            // Given
            // 포인트 초기화 (사용자 포인트 생성)
            Long userId = 1L;
            Long initialAmount = 1000L;
            PointModel pointModel = PointModel.of(initialAmount, userId);
            pointRepository.save(pointModel);

            // 동시에 발생할 충전 금액과 사용 금액
            Long chargeAmount = 500L;
            Long useAmount = 300L;

            // 두 개의 스레드로 동시에 충전과 사용 요청
            int numberOfThreads = 2; // 충전 1개, 사용 1개
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch readyLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 준비될 때까지 대기
            CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호를 위한 래치
            CountDownLatch completionLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 완료될 때까지 대기

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            AtomicBoolean chargeSuccess = new AtomicBoolean(false);
            AtomicBoolean useSuccess = new AtomicBoolean(false);

            // When
            // 충전 스레드
            executorService.submit(() -> {
                try {
                    // 준비 완료 신호
                    readyLatch.countDown();
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    // 포인트 충전 요청
                    pointService.chargePoint(new PointCommand.ChargePoint(userId, chargeAmount));
                    successCount.incrementAndGet();
                    chargeSuccess.set(true);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    completionLatch.countDown();
                }
            });

            // 사용 스레드
            executorService.submit(() -> {
                try {
                    // 준비 완료 신호
                    readyLatch.countDown();
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    // 포인트 사용 요청
                    pointService.usePoint(new PointCommand.UsePoint(userId, useAmount));
                    successCount.incrementAndGet();
                    useSuccess.set(true);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    completionLatch.countDown();
                }
            });

            // 모든 스레드가 준비될 때까지 대기
            readyLatch.await();
            // 모든 스레드에게 시작 신호
            startLatch.countDown();
            // 모든 스레드가 완료될 때까지 대기
            completionLatch.await();
            executorService.shutdown();

            // Then
            // 최종 포인트 조회
            PointModel updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            Long finalAmount = updatedPoint.getAmount().getAmount();

            // 비관적 락을 사용하면 두 요청 모두 성공해야 함
            assertAll(
                // 두 요청 모두 성공
                () -> assertThat(successCount.get()).isEqualTo(2),
                // 실패 없음
                () -> assertThat(failCount.get()).isEqualTo(0),
                // 충전 성공
                () -> assertThat(chargeSuccess.get()).isTrue(),
                // 사용 성공
                () -> assertThat(useSuccess.get()).isTrue(),
                // 최종 포인트는 초기 금액 + 충전 금액 - 사용 금액
                () -> assertThat(finalAmount).isEqualTo(initialAmount + chargeAmount - useAmount)
            );
        }
    }
}
