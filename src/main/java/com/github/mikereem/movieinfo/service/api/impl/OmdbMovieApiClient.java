package com.github.mikereem.movieinfo.service.api.impl;

import com.github.mikereem.movieinfo.client.omdb.api.DefaultApi;
import com.github.mikereem.movieinfo.client.omdb.model.CombinedResult;
import com.github.mikereem.movieinfo.dto.Movie;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.exception.InvalidApiResponseException;
import com.github.mikereem.movieinfo.service.api.MovieApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.mikereem.movieinfo.config.CacheConfiguration.OMDB_DIRECTORS_CACHE_NAME;
import static com.github.mikereem.movieinfo.config.CacheConfiguration.OMDB_SEARCH_CACHE_NAME;

@Slf4j
@Service("omdb")
@RequiredArgsConstructor
public class OmdbMovieApiClient implements MovieApiClient {
    private static final String DIRECTORS_DELIMITER = ", ";
    private static final int PAGE_SIZE = 10;
    private final DefaultApi defaultApi;

    @Value("${movie-api.omdb-key}")
    private String apiKey;

    @Override
    @Cacheable(value = OMDB_SEARCH_CACHE_NAME, key = "{#title, #page}", unless = "#result == null or #result.movies.isEmpty()")
    public PagedSearchResult searchMovies(String title, int page) {
        log.info("Searching movies for title {} and page {}", title, page);

        CombinedResult result = defaultApi.getOMDbSearch("", apiKey, null, null, title, null, null, null, null, null, page, null);
        if (!isSuccessful(result.getResponse())) {
            throw new InvalidApiResponseException();
        }
        if (result.getSearch() == null) {
            return PagedSearchResult.empty();
        }

        return PagedSearchResult.builder()
                .movies(result.getSearch().stream().map(m -> new Movie(m.getImdbID(), m.getTitle(), m.getYear(), List.of()))
                        .toList())
                .totalPages(getTotalPages(result.getTotalResults()))
                .build();
    }

    @Override
    @Cacheable(value = OMDB_DIRECTORS_CACHE_NAME, key = "#movieId", unless = "#result == null or #result.isEmpty()")
    public List<String> getMovieDirectors(String movieId) {
        log.info("Searching movie directors for movieId {}", movieId);

        CombinedResult result = defaultApi.getOMDbSearch("", apiKey, null, movieId, "", null, null, null, null, null, null, null);
        if (result == null || result.getDirector() == null) {
            return List.of(DIRECTOR_NOT_FOUND);
        }
        return List.of(result.getDirector().split(DIRECTORS_DELIMITER));
    }

    int getTotalPages(String totalResults) {
        int parsedTotalResults = 0;
        if (totalResults != null) {
            try {
                parsedTotalResults = Integer.parseInt(totalResults);
            } catch (NumberFormatException e) {
                log.warn("Invalid total results: {}", totalResults);
            }
        }
        return (int) Math.ceil((double) parsedTotalResults / PAGE_SIZE);
    }

    boolean isSuccessful(String response) {
        return Boolean.parseBoolean(response);
    }
}
