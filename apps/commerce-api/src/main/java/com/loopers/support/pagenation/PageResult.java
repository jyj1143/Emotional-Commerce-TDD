package com.loopers.support.pagenation;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    /**
     * 페이지 결과 타입 변환 (content만 변환)
     */
    public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mappedContent = this.content.stream()
            .map(mapper)
            .collect(Collectors.toList());

        return new PageResult<>(mappedContent, this.paginationInfo);
    }

    /**
     * 빈 페이지 결과 생성
     */
    public static <T> PageResult<T> empty() {
        PaginationInfo emptyPagination = new PaginationInfo(
                1,       // currentPage
                1,       // totalPage
                0,       // pageSize
                0L,      // totalCount
                false,   // hasNextPage
                false    // hasPreviousPage
        );
        return new PageResult<>(List.of(), emptyPagination);
    }

}
