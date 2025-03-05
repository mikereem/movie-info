package com.github.mikereem.movieinfo.service.api;

import com.github.mikereem.movieinfo.dto.PagedSearchResult;

import java.util.List;

public interface MovieApiClient {
    String DIRECTOR_NOT_FOUND = "N/A";

    PagedSearchResult searchMovies(String title, int page);

    List<String> getMovieDirectors(String movieId);
}
