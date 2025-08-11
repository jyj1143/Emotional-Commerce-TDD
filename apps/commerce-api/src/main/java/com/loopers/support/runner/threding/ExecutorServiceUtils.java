package com.loopers.support.runner.threding;

import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 스레드 풀 관리 유틸리티
 */

@Slf4j
public class ExecutorServiceUtils {
    /**
     * ExecutorService를 안전하게 종료합니다.
     *
     * @param executorService 종료할 ExecutorService
     * @param timeout         종료 대기 시간
     * @param unit            시간 단위
     */
    public static void shutdownGracefully(ExecutorService executorService, long timeout, TimeUnit unit) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, unit)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Thread pool termination interrupted", e);
        }
    }
}
