package com.loopers.interfaces.event.product;


import com.loopers.domain.like.dto.LikeEvent;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.product.dto.product.ProductEvent;
import com.loopers.domain.product.enums.SaleStatus;
import com.loopers.domain.product.service.ProductSignalService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@MockitoSettings
class ProductSignalEventListenerTest {

    @InjectMocks
    private ProductSignalEventListener sut;

    @Mock
    private ProductSignalService productSignalService;

    @DisplayName("상품 생성을 때,")
    @Nested
    class ProductCreated {

        @DisplayName("상품 시그널을 생성한다.")
        @Test
        void createProductSignal() {
            // given
            Long productId = 1L;
            String name = "테스트 상품";
            Long price = 10000L;
            SaleStatus saleStatus = SaleStatus.ON_SALE;
            LocalDate saleDate = LocalDate.now();

            ProductEvent.Register event = new ProductEvent.Register(
                productId, name, price, saleStatus, saleDate
            );

            // when
            sut.handleRegisterProductSignal(event);

            // then
            verify(productSignalService, times(1)).register(event.productId());
        }
    }

    @DisplayName("상품 좋아요를 할 때,")
    @Nested
    class ProductLike {

        @DisplayName("상품 시그널의 좋아요 수를 증가시킨다.")
        @Test
        void increaseLikeCount() {
            // given
            Long targetId = 1L;
            LikeEvent.Like event = LikeEvent.Like.of(targetId, LikeType.PRODUCT);

            // when
            sut.handleIncreaseLikeCount(event);

            // then
            verify(productSignalService, times(1)).increaseLikeCount(event.targetId());
        }
    }

    @DisplayName("상품 좋아요를 취소 할 때,")
    @Nested
    class ProductUnLike {

        @DisplayName("상품 시그널의 좋아요 수를 감소시킨다.")
        @Test
        void decreaseLikeCount() {
            // given
            Long targetId = 1L;
            LikeEvent.UnLike event = LikeEvent.UnLike.of(targetId, LikeType.PRODUCT);

            // when
            sut.handleDecreaseLikeCount(event);

            // then
            verify(productSignalService, times(1)).decreaseLikeCount(event.targetId());
        }
    }
}
