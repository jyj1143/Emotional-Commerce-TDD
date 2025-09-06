package com.loopers.domain.product.service;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.dto.ProductCriteria;
import com.loopers.application.product.dto.ProductInfo;
import com.loopers.application.product.dto.ProductResult;
import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.repository.BrandRepository;
import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.repository.LikeRepository;
import com.loopers.domain.product.entity.ProductFixture;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class ProductFacadeIntegrationTest {
    @Autowired
    private ProductFacade productFacade;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품상세 조회할 때, ")
    @Nested
    class Get {



        @DisplayName("상품 ID로 해당하는 상품상세를 조회한다.")
        @Test
        void when_productIdGiven_then_getProductDetail() {
            // given
            String brandName = "나이키";
            int expectLikeCount = 4;

            BrandModel brandModel = brandRepository.save(BrandModel.of(brandName));
            List<ProductModel> productList = ProductFixture.createProductList(brandModel.getId());
            List<ProductModel> saved = productRepository.saveAll(productList);

            ProductModel first = saved.get(0);
            for (int i = 1; i <= expectLikeCount; i++) {
                likeRepository.save(LikeModel.of((long) i, first.getId(), LikeType.PRODUCT));
            }


            // when
            ProductResult productDetail = productFacade.getProductDetail(new ProductCriteria.GetProduct(first.getId(), null)  );
            assertAll(
                    () -> assertThat(productDetail.id()).isEqualTo(first.getId()),
                    () -> assertThat(productDetail.brandId()).isEqualTo(brandModel.getId()),
                    () -> assertThat(productDetail.brandName()).isEqualTo(brandName),
                    () -> assertThat(productDetail.likeCount()).isEqualTo(expectLikeCount)
            );

        }


    }


}
