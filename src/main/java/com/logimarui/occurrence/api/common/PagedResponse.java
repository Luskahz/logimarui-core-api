package com.logimarui.occurrence.api.common;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <S, T> PagedResponse<T> from(Page<S> result, Function<S, T> mapper) {
        return new PagedResponse<>(
                result.getContent().stream().map(mapper).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(),
                result.isFirst(), result.isLast()
        );
    }
}
