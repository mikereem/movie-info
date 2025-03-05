package com.github.mikereem.movieinfo.service;

import com.github.mikereem.movieinfo.dto.Movie;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.dto.SearchParams;
import com.github.mikereem.movieinfo.exception.UnknownApiException;
import com.github.mikereem.movieinfo.service.api.MovieApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.mikereem.movieinfo.config.CacheConfiguration.MOVIE_CACHE_NAME;

@Slf4j
@Service
public class MovieService {
    private final Map<String, MovieApiClient> movieApiClients;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    MovieService(Map<String, MovieApiClient> movieApiClients) {
        this.movieApiClients = movieApiClients;
    }

    @Cacheable(value = MOVIE_CACHE_NAME,
            key = "{#searchParams.apiName, #searchParams.movieTitle, #searchParams.page}",
            unless = "#result == null or #result.movies.isEmpty()")
    public PagedSearchResult getMovies(SearchParams searchParams) {
        log.info("Getting the list of movies for title {} with API {} for page {}",
                searchParams.getMovieTitle(), searchParams.getApiName(), searchParams.getPage());

        MovieApiClient movieApiClient = movieApiClients.get(searchParams.getApiName().toLowerCase());
        if (movieApiClient == null) {
            throw new UnknownApiException(searchParams.getApiName());
        }

        PagedSearchResult movies = movieApiClient.searchMovies(searchParams.getMovieTitle(), searchParams.getPage());

        List<CompletableFuture<Movie>> futures = movies.getMovies().stream()
                .map(m -> CompletableFuture.supplyAsync(() -> {
                    m.setDirectors(movieApiClient.getMovieDirectors(m.getMovieId()));
                    return m;
                }, executor))
                .toList();
        return PagedSearchResult.builder()
                .totalPages(movies.getTotalPages())
                .movies(futures.stream()
                        .map(CompletableFuture::join)
                        .toList())
                .build();
    }
}
