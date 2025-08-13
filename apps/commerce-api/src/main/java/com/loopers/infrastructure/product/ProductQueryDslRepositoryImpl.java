package com.loopers.infrastructure.product;

import com.loopers.domain.brand.entity.QBrandModel;
import com.loopers.domain.like.QLikeModel;
import com.loopers.domain.like.enums.LikeType;
import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.dto.summary.ProductSummaryInfo;
import com.loopers.domain.product.entity.QProductModel;
import com.loopers.domain.product.enums.ProductSortType;
import com.loopers.infrastructure.product.dto.ProductInfo.ProductWithBrand;
import com.loopers.support.pagenation.PageResult;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductQueryDslRepositoryImpl {

    private final JPAQueryFactory queryFactory;
    private static final QProductModel productModel = QProductModel.productModel;
    private static final QBrandModel brandModel = QBrandModel.brandModel;
    private static final QLikeModel likeModel = QLikeModel.likeModel;


    public List<ProductWithBrand> findProductWithBrand() {
        JPAQuery<ProductWithBrand> query = queryFactory.select(
                Projections.constructor(
                    ProductWithBrand.class,
                    productModel.id,
                    productModel.name.name,
                    productModel.salePrice.amount,
                    brandModel.id,
                    brandModel.name.name
                )
            )
            .from(productModel)
            .leftJoin(brandModel).on(productModel.refBrandId.eq(brandModel.id));

        return query.fetch();
    }

    public PageResult<ProductSummaryInfo> findProductSummary(ProductCommand.ProductSummary criteria) {
        Pageable pageable =
            PageRequest.of(criteria.page() - 1
                , criteria.size()
            );

        // 좋아요 수를 계산하는 서브쿼리
        JPQLQuery<Long> likeCount = JPAExpressions
            .select(likeModel.count())
            .from(likeModel)
            .where(
                likeModel.targetId.eq(productModel.id),
                likeModel.likeType.eq(LikeType.PRODUCT)
            )
            ;

        JPAQuery<ProductSummaryInfo> query = queryFactory.select(
                Projections.constructor(
                    ProductSummaryInfo.class,
                    productModel.id,
                    productModel.name.name,
                    productModel.salePrice.amount,
                    productModel.saleDate.saleDate,
                    brandModel.id,
                    brandModel.name.name,
                    likeCount
                )
            )
            .from(productModel)
            .leftJoin(brandModel).on(productModel.refBrandId.eq(brandModel.id))
            .orderBy(sortByField(criteria, likeCount))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        List<ProductSummaryInfo> content = query.fetch();
        Page<ProductSummaryInfo> pageResult = PageableExecutionUtils.getPage(content, pageable, query::fetchCount);

        return PageResult.of(pageResult);
    }

    private OrderSpecifier<?> sortByField(ProductCommand.ProductSummary criteria,  JPQLQuery<Long> likeCount) {
        Order order = Order.valueOf(criteria.sortOrder().name());
        ProductSortType productSortType = criteria.sortType();
        if (productSortType == ProductSortType.LATEST) {
            return new OrderSpecifier<>(order, productModel.saleDate.saleDate);
        }

        if (productSortType == ProductSortType.SALE_PRICE) {
            return new OrderSpecifier<>(order, productModel.salePrice.amount);
        }

        if (productSortType == ProductSortType.LIKE) {
            return new OrderSpecifier<>(order, likeCount);
        }
        return new OrderSpecifier<>(Order.DESC, productModel.id);
    }


}
