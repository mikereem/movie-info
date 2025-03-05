package com.github.mikereem.movieinfo.controller;

import com.github.mikereem.movieinfo.dto.ErrorResponse;
import com.github.mikereem.movieinfo.dto.MovieResponse;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.dto.SearchParams;
import com.github.mikereem.movieinfo.event.pub.SearchPublisher;
import com.github.mikereem.movieinfo.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/movies")
@AllArgsConstructor
@Tag(name = "Movie Search API")
public class MovieController {

    private final MovieService movieService;
    private final SearchPublisher searchPublisher;

    @GetMapping("/{movieTitle}")
    @Operation(
            summary = "Search for movies",
            description = "Search for movies by title using the selected API (omdb or tmdb). Pagination is supported."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PagedSearchResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MovieResponse getMovies(
            @Parameter(description = "Title or part of the title of the movie", example = "Inception")
            @PathVariable String movieTitle,
            @Parameter(description = "API source (either 'omdb' or 'tmdb')", example = "omdb")
            @RequestParam String apiName,
            @Parameter(description = "Page number for paginated results", example = "1")
            @RequestParam(defaultValue = "1") int page) {
        log.info("GET movies request with movieTitle {} and apiName {} for page {}", movieTitle, apiName, page);
        SearchParams searchParams = new SearchParams(movieTitle, apiName, page);
        searchPublisher.publish(searchParams);
        PagedSearchResult pagedSearchResult = movieService.getMovies(searchParams);
        return MovieResponse.builder()
                .movies(pagedSearchResult.getMovies())
                .totalPages(pagedSearchResult.getTotalPages())
                .build();
    }
}
