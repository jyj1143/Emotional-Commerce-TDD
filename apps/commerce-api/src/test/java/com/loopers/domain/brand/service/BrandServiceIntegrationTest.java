package com.loopers.domain.brand.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.brand.dto.BrandInfo;
import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.repository.BrandRepository;
import com.loopers.domain.product.entity.ProductFixture;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BrandServiceIntegrationTest {

    @Autowired
    private BrandService brandService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("브랜드조회 시,")
    @Nested
    class FindBrand {

        @BeforeEach
        void setUp() {

        }

        @DisplayName("존재하는 브랜드 ID를 조회하면, 해당 브랜드 정보를 반환한다.")
        @Test
        void whenExistingId_thenReturnBrand() {
            // given
            String brandName = "나이키";
            BrandModel brandModel = brandRepository.save(BrandModel.of(brandName));
            Long id = brandModel.getId();
            // when
            BrandInfo findBrand = brandService.get(id);
            // then
            assertAll(
                () -> assertThat(findBrand).isNotNull(),
                () -> assertThat(findBrand.name()).isEqualTo(brandName),
                () -> assertThat(findBrand.id()).isEqualTo(id)
            );
        }

        @DisplayName("존재하지 않는 브랜드 ID를 조회하면, CoreException 예외가 발생한다.")
        @Test
        void whenNotExistingId_thenThrowException() {
            // given
            Long id = 9999L; // 존재하지 않는 ID
            // when
            // then
            assertThrows(CoreException.class, () -> {
                brandService.get(id);
            });
        }


    }
}
