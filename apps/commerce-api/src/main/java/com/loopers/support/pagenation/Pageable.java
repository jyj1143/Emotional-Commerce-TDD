package com.loopers.support.pagenation;

import lombok.Getter;

@Getter
public class Pageable {
    private final int page;
    private final int size;

    public Pageable() {
        this(1, 10);
    }

    public Pageable(int page, int size) {
        this.page = Math.max(page, 1);
        this.size = Math.max(size, 1);
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public int getLimit() {
        return size;
    }
}

