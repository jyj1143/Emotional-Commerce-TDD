package com.loopers.domain.brand.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BrandModelTest {
    @DisplayName("상품 모델을 생성할 때, ")
    @Nested
    class Create {

        @DisplayName("브랜드 이름이 null이 아니면 성공한다.")
        @Test
        void given_validBrandName_when_createBrand_then_success() {
            // when
            String brandName = "나이키";
            BrandModel brand = BrandModel.of(brandName);
            // then
            assertAll(
                () -> assertNotNull(brand.getName()),
                () -> assertEquals(brandName, brand.getName().getName())
            );
        }

        @DisplayName("브랜드 이름이 null이면 CoreException 예외가 발생한다.")
        @Test
        void given_nullBrandName_when_createBrand_then_success() {
            // when
            String brandName = null;

            // then
            assertThrows(CoreException.class, () -> {
                BrandModel.of(brandName);
            });
        }

        @DisplayName("브랜드 이름이 비어있음면이상이면 CoreException 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {
            "", " ",  "\n", "\t",
        })
        void given_invalidBrandName_when_createBrand_thenThrowCoreException(String invalidBrandName) {
            // when
            // then
            assertThrows(CoreException.class, () -> {
                BrandModel.of(invalidBrandName);
            });
        }

        @DisplayName("브랜드 이름이 null이면 CoreException 예외가 발생한다.")
        @Test
        void given_null_when_createBrand_thenThrowCoreException() {
            // when, then
            assertThrows(CoreException.class, () -> {
                BrandModel.of(null);
            });
        }
    }
}
