package com.loopers.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.repository.BrandRepository;
import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.like.repository.LikeRepository;
import com.loopers.domain.like.service.LikeService;
import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.product.ProductCommand.ProductSummary;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.entity.ProductFixture;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.enums.ProductSortType;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.SortOrder;
import com.loopers.utils.DatabaseCleanUp;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProductSummaryServiceIntegrationTest {

    @Autowired
    private ProductSummaryService sut; // System under test
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private LikeService likeService;


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품조회 시,")
    @Nested
    class FindProductWithBrand {

        String brandName = "나이키";

        @BeforeEach
        void setUp() {
            BrandModel brandModel = brandRepository.save(BrandModel.of(brandName));
            List<ProductModel> productList = ProductFixture.createProductList(brandModel.getId());
            List<ProductModel> saved = productRepository.saveAll(productList);


            ProductModel first = saved.get(0);
            likeRepository.save(LikeModel.of(1L, first.getId(), LikeType.PRODUCT));
            likeRepository.save(LikeModel.of(2L, first.getId(), LikeType.PRODUCT));
            likeRepository.save(LikeModel.of(3L, first.getId(), LikeType.PRODUCT));
            likeRepository.save(LikeModel.of(4L, first.getId(), LikeType.PRODUCT));

            ProductModel second = saved.get(1);
            likeRepository.save(LikeModel.of(1L, second.getId(), LikeType.PRODUCT));
            likeRepository.save(LikeModel.of(2L, second.getId(), LikeType.PRODUCT));
            likeRepository.save(LikeModel.of(3L, second.getId(), LikeType.PRODUCT));

            ProductModel third = saved.get(2);
            likeRepository.save(LikeModel.of(1L, third.getId(), LikeType.PRODUCT));
            likeRepository.save(LikeModel.of(2L, third.getId(), LikeType.PRODUCT));
        }

        @DisplayName("상품과 브랜드 정보를 함께 조회한다.")
        @Test
        void whenJoinWithExistingId_thenThrowsException() {
            // given
            ProductCommand.ProductSummary criteria = new ProductSummary(1, 10, ProductSortType.SALE_PRICE, SortOrder.DESC);
            // when
            PageResult<ProductSummaryInfo> result = sut.findAll(criteria);
            List<ProductSummaryInfo> content = result.content();
            // then
            assertThat(content).isNotEmpty();
            content.forEach(product ->
                assertThat(product.brandName()).isEqualTo(brandName)
            );
        }

        @DisplayName("상품의 정렬 조건(Price)에 따라 가격순으로 조회된다.")
        @Test
        void when_findAll_then_sortedByPrice() {
            // given
            ProductCommand.ProductSummary criteria = new ProductSummary(1, 10, ProductSortType.SALE_PRICE, SortOrder.DESC);
            // when
            PageResult<ProductSummaryInfo> result = sut.findAll(criteria);
            List<ProductSummaryInfo> content = result.content();

            // then
            assertThat(content).isNotEmpty();
            assertThat(content).isSortedAccordingTo(Comparator.comparing(
                item -> item.salePrice(),
                Comparator.reverseOrder()
            ));
        }


        @DisplayName("상품의 정렬 조건(Like)에 따라 좋아요순으로 조회된다.")
        @Test
        void when_findAll_then_sortedByLike() {
            // given
            ProductCommand.ProductSummary criteria = new ProductSummary(1, 10, ProductSortType.LIKE, SortOrder.DESC);
            // when
            PageResult<ProductSummaryInfo> result = sut.findAll(criteria);
            List<ProductSummaryInfo> content = result.content();

            // then
            assertThat(content).isNotEmpty();
            assertThat(content).isSortedAccordingTo(Comparator.comparing(
                item -> item.likeCount(),
                Comparator.reverseOrder()
            ));
        }


    }

}
