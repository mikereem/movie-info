package com.github.mikereem.movieinfo.service.api.impl;

import com.github.mikereem.movieinfo.client.tmdb.api.DefaultApi;
import com.github.mikereem.movieinfo.client.tmdb.model.MovieCredits200Response;
import com.github.mikereem.movieinfo.client.tmdb.model.MovieCredits200ResponseCrewInner;
import com.github.mikereem.movieinfo.client.tmdb.model.SearchMovie200Response;
import com.github.mikereem.movieinfo.dto.Movie;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.exception.InvalidApiResponseException;
import com.github.mikereem.movieinfo.service.api.MovieApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.mikereem.movieinfo.config.CacheConfiguration.TMDB_DIRECTORS_CACHE_NAME;
import static com.github.mikereem.movieinfo.config.CacheConfiguration.TMDB_SEARCH_CACHE_NAME;

@Slf4j
@Service("tmdb")
@RequiredArgsConstructor
public class TmdbMovieApiClient implements MovieApiClient {
    private final static String JOB_DIRECTOR = "Director";
    private final DefaultApi defaultApi;

    @Override
    @Cacheable(value = TMDB_SEARCH_CACHE_NAME, key = "{#title, #page}", unless = "#result == null or #result.movies.isEmpty()")
    public PagedSearchResult searchMovies(String title, int page) {
        log.info("Searching movies for title {} and page {}", title, page);

        try {
            SearchMovie200Response result = defaultApi.searchMovie(title, true, null, null, page, null, null);
            if (result.getResults() == null || result.getResults().isEmpty()) {
                return PagedSearchResult.empty();
            }
            return PagedSearchResult.builder()
                    .movies(result.getResults().stream()
                            .map(m -> new Movie(String.valueOf(m.getId()), m.getTitle(), extractYear(m.getReleaseDate()), List.of()))
                            .toList())
                    .totalPages(getTotalPages(result.getTotalPages()))
                    .build();
        } catch (RestClientException e) {
            log.warn("Error during TMDB search", e);
            throw new InvalidApiResponseException();
        }
    }

    @Override
    @Cacheable(value = TMDB_DIRECTORS_CACHE_NAME, key = "#movieId", unless = "#result == null or #result.isEmpty()")
    public List<String> getMovieDirectors(String movieId) {
        log.info("Searching movie directors for movieId {}", movieId);

        MovieCredits200Response result = defaultApi.movieCredits(Integer.parseInt(movieId), null);

        if (result == null || result.getCrew() == null) {
            return List.of(DIRECTOR_NOT_FOUND);
        }
        return result.getCrew().stream()
                .filter(c -> JOB_DIRECTOR.equalsIgnoreCase(c.getJob()))
                .map(MovieCredits200ResponseCrewInner::getName)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.isEmpty() ? List.of(DIRECTOR_NOT_FOUND) : list
                ));
    }

    String extractYear(String input) {
        if (input == null) {
            return "";
        }

        String trimmed = input.trim();
        if (trimmed.length() < 4) {
            return trimmed;
        }

        return trimmed.substring(0, 4);
    }

    int getTotalPages(Integer totalPages) {
        if (totalPages == null) {
            return 0;
        }
        return totalPages;
    }

}
