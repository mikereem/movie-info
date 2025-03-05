package com.github.mikereem.movieinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MovieResponse {
    private List<Movie> movies;
    private final int totalPages;
}
