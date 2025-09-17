package com.loopers;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@EnableBatchProcessing
@SpringBootApplication
@ConfigurationPropertiesScan
public class CommerceBatchApplication {

    @PostConstruct
    public void started() {
        // set timezone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(CommerceBatchApplication.class, args);
    }
}
