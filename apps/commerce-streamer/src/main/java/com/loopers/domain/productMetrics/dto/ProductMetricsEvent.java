package com.loopers.domain.productMetrics.dto;

import java.util.List;

public record ProductMetricsEvent() {

    public record LikeList(
        List<Like> likes
    ) {
        public record Like(
            Long productId
        ) {

            public static Like of(Long productId) {
                return new Like(productId);
            }
        }
    }


    public record UnLikeList(
        List<UnLike> likes
    ) {
        public record UnLike(
            Long productId
        ) {

            public static UnLike of(Long productId) {
                return new UnLike(productId);
            }
        }
    }

    public record ViewList(
        List<View> views
    ){
        public record View(
            Long productId
        ) {

            public static View of(Long productId) {
                return new View(productId);
            }
        }
    }

    public record OrderList(
        List<Order> orders
    ){
        public record Order(
            Long productId
        ) {

            public static Order of(Long productId) {
                return new Order(productId);
            }
        }
    }

}
