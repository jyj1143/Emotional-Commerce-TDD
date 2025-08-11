package com.loopers.support.runner;


import com.loopers.support.runner.initializer.ProductInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
@RequiredArgsConstructor
public class DataInitializerRunner implements ApplicationRunner {

    private final ProductInitializer productInitializer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("더미 데이터 초기화 시작");

        try {
            productInitializer.bulkInsertProductsWithMultiThreading();
            log.info("더미 데이터 초기화 완료");
        } catch (Exception e) {
            log.error("더미 데이터 생성 중 오류 발생", e);
        }
    }
}
