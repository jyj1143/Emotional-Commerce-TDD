package com.loopers.domain.inventory.service;

import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.dto.InventoryCommand;
import com.loopers.domain.inventory.dto.InventoryCommand.GetInventory;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.repository.ProductSkuRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryIntegrationServiceTest {


    @Autowired
    private InventoryService sut;
    @Autowired
    private ProductSkuRepository productSkuRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PointRepository pointRepository;


    @DisplayName("재고 감소시킬때, ")
    @Nested
    class Decrease {
        private static final Long TEST_STOCK_SIZE = 50L;
        @DisplayName("동시에 재고 차감 성공한다.")
        @Test
        void given_concurrentRequests_when_decreaseStock_then_stockDecreasedCorrectly() throws InterruptedException {
            // Given
            // 상품 및 재고 생성
            ProductSkuModel productSkuModel = ProductSkuModel.of(0L,0L, "색상", "RED"
                , SaleStatus.ON_SALE, 1L);
            ProductSkuModel productSku = productSkuRepository.save(productSkuModel);
            inventoryRepository.save(InventoryModel.of(TEST_STOCK_SIZE, productSku.getId()));

            int numberOfThreads = 100; // 100개의 동시 요청
            long requestQuantity = 1L; // 각 요청당 1개씩 차감


            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                executorService.submit(() -> {
                    try {
                        InventoryCommand.DecreaseStock command = new InventoryCommand.DecreaseStock (productSku.getId(), requestQuantity);

                        sut.decrease(command);
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
            assertEquals(TEST_STOCK_SIZE, successCount.get());
            assertEquals(50, failCount.get());

            InventoryModel inventory = sut.getInventory(new GetInventory(productSku.getId()));
            int expectedTotalDecreased = (int)(successCount.get() * requestQuantity); // 예상 총 차감량
            // 재고가 정확히 차감되었는지 확인
            assertEquals(TEST_STOCK_SIZE - expectedTotalDecreased, inventory.getQuantity().getQuantity());
        }
    }
}
