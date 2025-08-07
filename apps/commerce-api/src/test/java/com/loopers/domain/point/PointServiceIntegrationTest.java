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

        @DisplayName("한번만 성공하고 나머지 ObjectOptimisticLockingFailureException가 발생한다")
        @Test
        void when_multipleConcurrentPointUsage_then_onlyOneSucceedsAndOthersFail() throws InterruptedException {
            // Given
            // 포인트 초기화 (사용자에게 충분한 포인트 지급)
            Long userId = 1L;
            Long initialAmount = 10000L;
            PointModel pointModel = PointModel.of(initialAmount, userId);
            pointRepository.save(pointModel);

            // 동시에 사용할 포인트 금액
            Long useAmount = 1000L;

            // 여러 스레드로 동시에 포인트 사용 요청
            int numberOfThreads = 5; // 동시 요청 수
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch readyLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 준비될 때까지 대기
            CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호를 위한 래치
            CountDownLatch completionLatch = new CountDownLatch(numberOfThreads); // 모든 스레드가 완료될 때까지 대기

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger optimisticLockExceptionCount = new AtomicInteger(0);
            AtomicInteger otherExceptionCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        // 준비 완료 신호
                        readyLatch.countDown();
                        // 모든 스레드가 동시에 시작하도록 대기
                        startLatch.await();

                        // 포인트 사용 요청
                        pointService.usePoint(new PointCommand.UsePoint(userId, useAmount));
                        successCount.incrementAndGet();
                    } catch (ObjectOptimisticLockingFailureException e) {
                        // 낙관적 락 예외 발생
                        optimisticLockExceptionCount.incrementAndGet();
                    } catch (Exception e) {
                        otherExceptionCount.incrementAndGet();
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
            // 데이터베이스에서 최종 포인트 조회
            PointModel updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            Long finalAmount = updatedPoint.getAmount().getAmount();

            assertAll(
                // 성공은 하나만 있어야 함
                () -> assertThat(successCount.get()).isEqualTo(1),
                // 낙관적 락 예외는 나머지 스레드 수만큼 발생해야 함
                () -> assertThat(optimisticLockExceptionCount.get()).isEqualTo(numberOfThreads - 1),
                // 다른 예외는 없어야 함
                () -> assertThat(otherExceptionCount.get()).isEqualTo(0),
                // 최종 포인트는 초기 금액에서 한 번의 사용 금액만 차감되어야 함
                () -> assertThat(finalAmount).isEqualTo(initialAmount - useAmount)
            );
        }
    }


    @DisplayName("동시에 포인트 충전 시,")
    @Nested
    class ConcurrencyChargePoint {

        @DisplayName("한번만 성공하고 나머지 ObjectOptimisticLockingFailureException가 발생한다")
        @Test
        void when_multipleConcurrentPointCharge_then_onlyOneSucceedsAndOthersFail() throws InterruptedException {
            // Given
            // 포인트 초기화 (사용자에게 충분한 포인트 지급)
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
            AtomicInteger optimisticLockExceptionCount = new AtomicInteger(0);
            AtomicInteger otherExceptionCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        // 준비 완료 신호
                        readyLatch.countDown();
                        // 모든 스레드가 동시에 시작하도록 대기
                        startLatch.await();

                        // 포인트 사용 요청
                        pointService.chargePoint(new PointCommand.ChargePoint(userId, chargeAmount));
                        successCount.incrementAndGet();
                    } catch (ObjectOptimisticLockingFailureException e) {
                        // 낙관적 락 예외 발생
                        optimisticLockExceptionCount.incrementAndGet();
                    } catch (Exception e) {
                        otherExceptionCount.incrementAndGet();
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
            // 데이터베이스에서 최종 포인트 조회
            PointModel updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            Long finalAmount = updatedPoint.getAmount().getAmount();

            assertAll(
                // 성공은 하나만 있어야 함
                () -> assertThat(successCount.get()).isEqualTo(1),
                // 낙관적 락 예외는 나머지 스레드 수만큼 발생해야 함
                () -> assertThat(optimisticLockExceptionCount.get()).isEqualTo(numberOfThreads - 1),
                // 다른 예외는 없어야 함
                () -> assertThat(otherExceptionCount.get()).isEqualTo(0),
                // 최종 포인트는 초기 금액에 한 번의 충전 금액만 더해져야 함
                () -> assertThat(finalAmount).isEqualTo(initialAmount + chargeAmount
                )
            );
        }
    }


    @DisplayName("동시에 포인트 충전 과 사용 시,")
    @Nested
    class ConcurrencyChargeAndUsePoint {
        @DisplayName("한번만 성공하고 나머지 ObjectOptimisticLockingFailureException가 발생한다")
        @Test
        void when_concurrentChargeAndUsePoint_then_oneFailsWithOptimisticLockingFailureException() throws InterruptedException {
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
            AtomicInteger optimisticLockExceptionCount = new AtomicInteger(0);
            AtomicInteger otherExceptionCount = new AtomicInteger(0);

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
                } catch (ObjectOptimisticLockingFailureException e) {
                    // 낙관적 락 예외 발생
                    optimisticLockExceptionCount.incrementAndGet();
                } catch (Exception e) {
                    otherExceptionCount.incrementAndGet();
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
                } catch (ObjectOptimisticLockingFailureException e) {
                    // 낙관적 락 예외 발생
                    optimisticLockExceptionCount.incrementAndGet();
                } catch (Exception e) {
                    otherExceptionCount.incrementAndGet();
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
            // 데이터베이스에서 최종 포인트 조회
            PointModel updatedPoint = pointRepository.findByUserId(userId).orElseThrow();
            Long finalAmount = updatedPoint.getAmount().getAmount();

            assertAll(
                // 성공은 1개만 있어야 함
                () -> assertThat(successCount.get()).isEqualTo(1),
                // 낙관적 락 예외는 1번 발생해야 함
                () -> assertThat(optimisticLockExceptionCount.get()).isEqualTo(1),
                // 다른 예외는 없어야 함
                () -> assertThat(otherExceptionCount.get()).isEqualTo(0),
                // 최종 포인트는 초기 금액에서 한 작업만 반영되어야 함
                () -> assertThat(finalAmount).isIn(Arrays.asList(initialAmount + chargeAmount, initialAmount - useAmount))
            );
        }
    }
}
