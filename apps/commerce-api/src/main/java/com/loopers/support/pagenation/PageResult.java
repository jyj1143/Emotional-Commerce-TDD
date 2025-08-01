package com.loopers.support.pagenation;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;


public record PageResult<T>(
    List<T> content,
    PaginationInfo paginationInfo
) {
    public static <T> PageResult<T> of(Page<T> page) {
        PaginationInfo pagination = new PaginationInfo(
            page.getNumber() + 1,
            page.getTotalPages(),
            page.getSize(),
            page.getTotalElements(),
            page.hasNext(),
            page.hasPrevious());


        return new PageResult<>(
            page.getContent(),
            pagination
        );
    }

    public record PaginationInfo(
        Integer currentPage,
        Integer totalPage,
        Integer pageSize,
        Long totalCount,
        Boolean hasNextPage,
        Boolean hasPreviousPage
    ) {
    }
}
