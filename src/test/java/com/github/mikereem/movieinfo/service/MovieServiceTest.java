package com.github.mikereem.movieinfo.service;

import com.github.mikereem.movieinfo.dto.Movie;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.dto.SearchParams;
import com.github.mikereem.movieinfo.exception.UnknownApiException;
import com.github.mikereem.movieinfo.service.api.MovieApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class MovieServiceTest {

    private static final String MOVIE_TITLE_1 = "Back to the Future";
    private static final String MOVIE_ID_1 = "id1";
    private static final String MOVIE_TITLE_2 = "Back to the Future Part II";
    private static final String MOVIE_ID_2 = "id2";
    private static final String MOVIE_YEAR_1 = "1985";
    private static final String MOVIE_YEAR_2 = "1989";
    private static final String MOVIE_DIRECTOR_1 = "Robert Zemeckis";
    private static final String MOVIE_DIRECTOR_2 = "Robert Zemeckis2";

    private MovieService movieService;

    @Mock
    private MovieApiClient movieApiClient;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        movieService = new MovieService(Map.of("testapi", movieApiClient));
    }

    @Test
    void getMovies_ShouldReturnMoviesWithDirectors_WhenValidApiIsUsed() {
        // Given
        SearchParams searchParams = new SearchParams(MOVIE_TITLE_1, "testapi", 1);
        List<Movie> movies = List.of(
                new Movie(MOVIE_ID_1, MOVIE_TITLE_1, MOVIE_YEAR_1, List.of()),
                new Movie(MOVIE_ID_2, MOVIE_TITLE_2, MOVIE_YEAR_2, List.of())
        );
        PagedSearchResult apiResult = new PagedSearchResult(movies, 3);

        when(movieApiClient.searchMovies(MOVIE_TITLE_1, 1)).thenReturn(apiResult);
        when(movieApiClient.getMovieDirectors(MOVIE_ID_1)).thenReturn(List.of(MOVIE_DIRECTOR_1));
        when(movieApiClient.getMovieDirectors(MOVIE_ID_2)).thenReturn(List.of(MOVIE_DIRECTOR_2));

        // When
        PagedSearchResult result = movieService.getMovies(searchParams);

        // Then
        assertThat(result.getMovies()).hasSize(2);
        assertThat(result.getMovies().get(0).getTitle()).isEqualTo(MOVIE_TITLE_1);
        assertThat(result.getMovies().get(0).getDirectors()).containsExactly(MOVIE_DIRECTOR_1);
        assertThat(result.getMovies().get(1).getTitle()).isEqualTo(MOVIE_TITLE_2);
        assertThat(result.getMovies().get(1).getDirectors()).containsExactly(MOVIE_DIRECTOR_2);

        verify(movieApiClient, times(1)).searchMovies(MOVIE_TITLE_1, 1);
        verify(movieApiClient, times(1)).getMovieDirectors(MOVIE_ID_1);
        verify(movieApiClient, times(1)).getMovieDirectors(MOVIE_ID_2);
    }

    @Test
    void getMovies_ShouldThrowUnknownApiException_WhenInvalidApiIsUsed() {
        // Given
        SearchParams searchParams = new SearchParams(MOVIE_TITLE_1, "invalidApi", 1);

        // When & Then
        assertThatThrownBy(() -> movieService.getMovies(searchParams))
                .isInstanceOf(UnknownApiException.class)
                .hasMessageContaining("invalidApi");

        verifyNoInteractions(movieApiClient);
    }
}
