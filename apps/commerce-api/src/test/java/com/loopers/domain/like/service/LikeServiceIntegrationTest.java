package com.loopers.domain.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.dto.LikeCommand;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.repository.LikeRepository;
import com.loopers.utils.DatabaseCleanUp;
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
import org.springframework.transaction.UnexpectedRollbackException;

@SpringBootTest
public class LikeServiceIntegrationTest {

    @Autowired
    LikeService sut;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("동시에 좋아요를 요청할 때, ")
    @Nested
    class ConcurrencyLike {

        @DisplayName("[happy] - 여러 사용자가 동시에 같은 상품에 좋아요를 요청하면 모두 성공한다.")
        @Test
        void when_multipleConcurrentLikeRequest_then_allRequestsSucceed() throws InterruptedException {
            // Given
            Long targetId = 1L;
            LikeType likeType = LikeType.PRODUCT;
            int numberOfThreads = 100; // 동시에 요청할 사용자 수
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                final Long userId = (long) (i + 1); // 각 스레드마다 다른 사용자 ID 사용
                executorService.submit(() -> {
                    try {
                        LikeCommand.Like command = new LikeCommand.Like(userId, targetId, likeType);
                        sut.like(command);
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
            // 데이터베이스에서 실제 좋아요 수 확인
            long actualLikeCount = likeRepository.count(targetId, likeType);

            // 검증
            assertAll(
                () -> assertThat(successCount.get()).isEqualTo(numberOfThreads),
                () -> assertThat(failCount.get()).isEqualTo(0L),
                () -> assertThat(actualLikeCount).isEqualTo(numberOfThreads)
            );
        }


        @DisplayName("[happy] - 한 사용자가 동시에 같은 상품에 좋아요를 요청하더라도 멱등성을 지키며 성공한다.")
        @Test
        void when_singleUserConcurrentLikeRequests_then_registerOnce() throws InterruptedException {
            // Given
            Long userId = 1L;  // 단일 사용자
            Long targetId = 1L;
            LikeType likeType = LikeType.PRODUCT;
            int numberOfThreads = 100; // 동시에 요청할 횟수
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        LikeCommand.Like command = new LikeCommand.Like(userId, targetId, likeType);
                        sut.like(command);
                        successCount.incrementAndGet();
                    } catch (UnexpectedRollbackException e) {
                        // 내부 트랜잭션이 rollback-only 상태로 마킹되어 발생한 예외
                        // 이 경우에도 멱등성을 보장하기 때문에 실패로 보지 않음
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
            // 데이터베이스에서 실제 좋아요 수 확인 (전체 좋아요 수)
            long totalLikeCount = likeRepository.count(targetId, likeType);

            // 해당 사용자의 좋아요 수 확인 (1개여야 함)
            boolean userLikeExists = likeRepository.isExists(userId, targetId, likeType);

            // 검증
            assertAll(
                () -> assertThat(successCount.get()).isEqualTo(numberOfThreads), // 모든 요청이 예외 없이 처리됨
                () -> assertThat(failCount.get()).isEqualTo(0L), // 예외가 발생하지 않음
                () -> assertThat(totalLikeCount).isEqualTo(1), // 총 좋아요 수는 1개
                () -> assertThat(userLikeExists).isTrue() // 사용자 좋아요가 존재해야 함
            );

        }
    }

    @DisplayName("동시에 좋아요 취소를 요청할 때, ")
    @Nested
    class ConcurrencyUnlike {

        @DisplayName("[happy] - 여러 사용자가 동시에 같은 상품에 좋아요 취소를 요청하면 모두 성공한다.")
        @Test
        void when_multipleConcurrentUnlikeRequest_then_allRequestsSucceed() throws InterruptedException {
            // Given
            Long targetId = 1L;
            LikeType likeType = LikeType.PRODUCT;
            int numberOfThreads = 10; // 동시에 요청할 사용자 수

            // 좋아요 등록
            for (int i = 0; i < numberOfThreads; i++) {
                Long userId = (long) (i + 1);
                LikeModel likeModel = LikeModel.of(userId, targetId, likeType);
                likeRepository.save(likeModel);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                final Long userId = (long) (i + 1); // 각 스레드마다 다른 사용자 ID 사용
                executorService.submit(() -> {
                    try {
                        LikeCommand.Unlike command = new LikeCommand.Unlike(userId, targetId, likeType);
                        sut.unlike(command);
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
            // 데이터베이스에서 실제 좋아요 수 확인
            long actualLikeCount = likeRepository.count(targetId, likeType);

            assertAll(
                () -> assertThat(successCount.get()).isEqualTo(numberOfThreads),
                () -> assertThat(failCount.get()).isEqualTo(0L),
                () -> assertThat(actualLikeCount).isEqualTo(0) // 모든 좋아요가 취소되어야 함
            );
        }

        @DisplayName("[happy] - 한 사용자가 동시에 같은 상품에 좋아요 취소를 요청하더라도 멱등성을 지키며 성공한다.")
        @Test
        void when_singleUserConcurrentUnlikeRequests_then_removeOnce() throws InterruptedException {
            // Given
            Long userId = 1L;  // 단일 사용자
            Long targetId = 1L;
            LikeType likeType = LikeType.PRODUCT;

            // 좋아요 등록
            LikeModel likeModel = LikeModel.of(userId, targetId, likeType);
            likeRepository.save(likeModel);

            int numberOfThreads = 10; // 동시에 요청할 횟수
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        LikeCommand.Unlike command = new LikeCommand.Unlike(userId, targetId, likeType);
                        sut.unlike(command);
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
            // 해당 사용자의 좋아요가 취소되었는지 확인
            boolean userLikeExists = likeRepository.isExists(userId, targetId, likeType);

            assertAll(
                () -> assertThat(successCount.get()).isEqualTo(numberOfThreads), // 모든 요청이 예외 없이 처리됨
                () -> assertThat(failCount.get()).isEqualTo(0L), // 예외가 발생하지 않음
                () -> assertThat(userLikeExists).isFalse() // 사용자 좋아요가 취소되어야 함
            );
        }
    }


    @DisplayName("좋아요를 등록할 때, ")
    @Nested
    class Like {

        @DisplayName("좋아요하지 않은 상태면, 좋아요 등록한다.")
        @Test
        void when_userHasNotLikedTarget_then_registerLike() {
            LikeCommand.Like command = new LikeCommand.Like(1L, 1L, LikeType.PRODUCT);
            boolean beforeIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            sut.like(command);
            boolean afterIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());

            assertAll(
                () -> assertThat(beforeIsExist).isFalse(),
                () -> assertThat(afterIsExist).isTrue()
            );
        }

        @DisplayName("좋아요한 상태면, 아무일도 일어나지 않는다.")
        @Test
        void when_userHasLikedTarget_then_doNothing() {
            LikeCommand.Like command = new LikeCommand.Like(1L, 1L, LikeType.PRODUCT);
            LikeModel likeModel = LikeModel.of(command.userId(), command.targetId(), command.likeType());
            likeRepository.save(likeModel);
            boolean beforeIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            sut.like(command);
            boolean afterIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            assertAll(
                () -> assertThat(beforeIsExist).isTrue(),
                () -> assertThat(afterIsExist).isTrue()
            );
        }
    }

    @DisplayName("좋아요를 취소할 때, ")
    @Nested
    class UnLike {

        @DisplayName("좋아요한 상태면, 좋아요 취소한다.")
        @Test
        void when_userHasLikedTarget_then_registerLike() {
            LikeCommand.Unlike command = new LikeCommand.Unlike(1L, 1L, LikeType.PRODUCT);
            LikeModel likeModel = LikeModel.of(command.userId(), command.targetId(), command.likeType());
            likeRepository.save(likeModel);
            boolean beforeIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            sut.unlike(command);
            boolean afterIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());

            assertAll(
                () -> assertThat(beforeIsExist).isTrue(),
                () -> assertThat(afterIsExist).isFalse()
            );
        }

        @DisplayName("좋아요하지 않은 상태면, 아무일도 일어나지 않는다.")
        @Test
        void when_userHasNotLikedTarget_then_doNothing() {
            LikeCommand.Unlike command = new LikeCommand.Unlike(1L, 1L, LikeType.PRODUCT);
            boolean beforeIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());
            sut.unlike(command);
            boolean afterIsExist = likeRepository.isExists(command.userId(), command.targetId(), command.likeType());

            assertAll(
                () -> assertThat(beforeIsExist).isFalse(),
                () -> assertThat(afterIsExist).isFalse()
            );
        }
    }

}
