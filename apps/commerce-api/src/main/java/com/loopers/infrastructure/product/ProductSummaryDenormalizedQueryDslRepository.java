package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.product.ProductCommand;
import com.loopers.domain.product.entity.QProductSummaryModel;
import com.loopers.domain.product.enums.ProductSortType;
import com.loopers.infrastructure.product.dto.ProductRow;
import com.loopers.support.pagenation.PageResult;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
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
public class ProductSummaryDenormalizedQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private static final QProductSummaryModel productSummaryModel = QProductSummaryModel.productSummaryModel;


    public PageResult<ProductRow.ProductSummary> findAll(ProductCommand.ProductSummary criteria) {
        Pageable pageable =
            PageRequest.of(criteria.page() - 1
                , criteria.size()
            );

        JPQLQuery<ProductRow.ProductSummary> query = queryFactory.select(
                Projections.constructor(
                    ProductRow.ProductSummary.class,
                    productSummaryModel.id,
                    productSummaryModel.productName,
                    productSummaryModel.salePrice,
                    productSummaryModel.saleDate,
                    productSummaryModel.refBrandId,
                    productSummaryModel.brandName,
                    productSummaryModel.likeCount
                )
            )
            .from(productSummaryModel)
            .orderBy(sortByField(criteria))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        List<ProductRow.ProductSummary> content = query.fetch();
        Page<ProductRow.ProductSummary> pageResult = PageableExecutionUtils.getPage(content, pageable, query::fetchCount);

        return PageResult.of(pageResult);
    }

    private OrderSpecifier<?> sortByField(ProductCommand.ProductSummary criteria) {
        Order order = Order.valueOf(criteria.sortOrder().name());
        ProductSortType productSortType = criteria.sortType();

        if (productSortType == ProductSortType.LATEST) {
            return new OrderSpecifier<>(order, productSummaryModel.saleDate.saleDate);
        }

        if (productSortType == ProductSortType.SALE_PRICE) {
            return new OrderSpecifier<>(order, productSummaryModel.salePrice.amount);
        }

        if (productSortType == ProductSortType.LIKE) {
            return new OrderSpecifier<>(order, productSummaryModel.likeCount.count);
        }
        return new OrderSpecifier<>(Order.DESC, productSummaryModel.id);
    }

}
