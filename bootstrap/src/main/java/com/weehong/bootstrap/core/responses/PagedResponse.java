package com.weehong.bootstrap.core.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Schema(description = "Paginated response wrapper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PagedResponse<T>(
    @Schema(description = "List of items in the current page")
    List<T> content,

    @Schema(description = "Pagination metadata")
    PageInfo page
) {

    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
            page.getContent(),
            new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
            )
        );
    }

    public static <T, R> PagedResponse<R> of(Page<T> page, Function<T, R> mapper) {
        return new PagedResponse<>(
            page.getContent().stream().map(mapper).toList(),
            new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
            )
        );
    }

    @Schema(description = "Pagination information")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PageInfo(
        @Schema(description = "Current page number (zero-based)", example = "0")
        int number,

        @Schema(description = "Number of items per page", example = "20")
        int size,

        @Schema(description = "Total number of items across all pages", example = "100")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages,

        @Schema(description = "Whether this is the first page", example = "true")
        boolean first,

        @Schema(description = "Whether this is the last page", example = "false")
        boolean last,

        @Schema(description = "Whether there is a next page", example = "true")
        boolean hasNext,

        @Schema(description = "Whether there is a previous page", example = "false")
        boolean hasPrevious
    ) {

    }

}
