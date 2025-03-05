package com.github.mikereem.movieinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Paginated search results containing movies")
public class PagedSearchResult implements Serializable {
    @Schema(description = "List of movies found by the search")
    private List<Movie> movies;
    @Schema(description = "Total number of pages available", example = "10")
    private int totalPages;

    public static PagedSearchResult empty() {
        return PagedSearchResult.builder()
                .movies(Collections.emptyList())
                .totalPages(0)
                .build();
    }
}
