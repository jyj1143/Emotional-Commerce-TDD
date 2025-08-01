package com.loopers.domain.product.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductModelTest {


    @DisplayName("상품 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("상품 가격이 양수이면 성공한다.")
        @ParameterizedTest
        @ValueSource(longs = {
            1,
            100,
            0
        })
        void given_validPrice_when_createProduct_then_success(Long validPrice) {

            // when
            ProductModel product = ProductModel.of("커피", validPrice, SaleStatus.ON_SALE, "2025-01-01", null);
            // then
            assertAll(
                () -> assertEquals(validPrice, product.getSalePrice().getAmount())
            );
        }

        @DisplayName("상품 가격이 음수이면 CoreException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(longs = {
            -1,
            -100,
        })
        void given_invalidPrice_when_createProduct_thenThrowCoreException(Long invalidPrice) {
            // when, then
            assertThrows(CoreException.class, () -> {
                ProductModel.of("커피", invalidPrice, SaleStatus.ON_SALE, "2025-01-01", null);
            });
        }

        @DisplayName("상품 이름이 비어있음면이상이면 CoreException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
            "", " ", "\n", "\t",
        })
        void given_invalidProductName_when_createBrand_thenThrowCoreException(String invalidProductName) {
            // when, then
            assertThrows(CoreException.class, () -> {
                ProductModel.of(invalidProductName, 1000L, SaleStatus.ON_SALE, "2025-01-01", null);
            });
        }

    }
}
