package com.github.mikereem.movieinfo.service.api.impl;

import com.github.mikereem.movieinfo.client.tmdb.api.DefaultApi;
import com.github.mikereem.movieinfo.client.tmdb.model.MovieCredits200Response;
import com.github.mikereem.movieinfo.client.tmdb.model.MovieCredits200ResponseCrewInner;
import com.github.mikereem.movieinfo.client.tmdb.model.SearchMovie200Response;
import com.github.mikereem.movieinfo.client.tmdb.model.SearchMovie200ResponseResultsInner;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.exception.InvalidApiResponseException;
import com.github.mikereem.movieinfo.service.api.MovieApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TmdbMovieApiClientTest {

    private static final String MOVIE_TITLE_1 = "Back to the Future";
    private static final Integer MOVIE_ID_1 = 1;
    private static final String MOVIE_TITLE_2 = "Back to the Future Part II";
    private static final Integer MOVIE_ID_2 = 2;
    private static final String MOVIE_YEAR_1 = "1985";
    private static final String MOVIE_YEAR_2 = "1989";
    private static final String MOVIE_DIRECTOR_1 = "Robert Zemeckis";

    @InjectMocks
    private TmdbMovieApiClient client;

    @Mock
    private DefaultApi defaultApi;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractYear_ValidDate_ShouldReturnYear() {
        assertEquals("2009", client.extractYear("2009-08-22"));
    }

    @Test
    void testExtractYear_NullInput_ShouldReturnEmptyString() {
        assertEquals("", client.extractYear(null));
    }

    @Test
    void testExtractYear_EmptyString_ShouldReturnEmptyString() {
        assertEquals("", client.extractYear(""));
    }

    @Test
    void testExtractYear_MoreThanFourCharacters_ShouldReturnFirstFourCharacters() {
        assertEquals("2015", client.extractYear("2015-06"));
    }

    @Test
    void getTotalPages_ShouldReturnSameNumber_WhenValidTotalPagesProvided() {
        assertThat(client.getTotalPages(1)).isEqualTo(1);
        assertThat(client.getTotalPages(10)).isEqualTo(10);
        assertThat(client.getTotalPages(100)).isEqualTo(100);
    }

    @Test
    void getTotalPages_ShouldReturnZero_WhenTotalPagesIsNull() {
        assertThat(client.getTotalPages(null)).isEqualTo(0);
    }

    @Test
    void searchMovies_ShouldReturnMovies_WhenApiResponseIsValid() {
        // Given
        String title = MOVIE_TITLE_1;
        int page = 1;
        SearchMovie200Response mockResponse = new SearchMovie200Response();
        mockResponse.setResults(List.of(
                new SearchMovie200ResponseResultsInner().id(MOVIE_ID_1).title(MOVIE_TITLE_1).releaseDate(MOVIE_YEAR_1),
                new SearchMovie200ResponseResultsInner().id(MOVIE_ID_2).title(MOVIE_TITLE_2).releaseDate(MOVIE_YEAR_2)
        ));
        mockResponse.setTotalPages(3);

        when(defaultApi.searchMovie(title, true, null, null, page, null, null))
                .thenReturn(mockResponse);

        // When
        PagedSearchResult result = client.searchMovies(title, page);

        // Then
        assertThat(result.getMovies()).hasSize(2);
        assertThat(result.getMovies().get(0).getTitle()).isEqualTo(MOVIE_TITLE_1);
        assertThat(result.getMovies().get(0).getYear()).isEqualTo(MOVIE_YEAR_1);
        assertThat(result.getMovies().get(1).getTitle()).isEqualTo(MOVIE_TITLE_2);
        assertThat(result.getMovies().get(1).getYear()).isEqualTo(MOVIE_YEAR_2);
        assertThat(result.getTotalPages()).isEqualTo(3);

        verify(defaultApi, times(1)).searchMovie(title, true, null, null, page, null, null);
    }

    @Test
    void searchMovies_ShouldReturnEmptyResult_WhenApiReturnsNull() {
        // Given
        String title = MOVIE_TITLE_1;
        int page = 1;
        SearchMovie200Response mockResponse = new SearchMovie200Response();

        when(defaultApi.searchMovie(title, true, null, null, page, null, null))
                .thenReturn(mockResponse);

        // When
        PagedSearchResult result = client.searchMovies(title, page);

        // Then
        assertThat(result.getMovies()).isEmpty();
        assertThat(result.getTotalPages()).isEqualTo(0);

        verify(defaultApi, times(1)).searchMovie(title, true, null, null, page, null, null);
    }

    @Test
    void searchMovies_ShouldThrowInvalidApiResponseException_WhenApiCallFails() {
        // Given
        String title = MOVIE_TITLE_1;
        int page = 1;

        when(defaultApi.searchMovie(title, true, null, null, page, null, null))
                .thenThrow(new RestClientException("API Error"));

        // When & Then
        assertThatThrownBy(() -> client.searchMovies(title, page))
                .isInstanceOf(InvalidApiResponseException.class);

        verify(defaultApi, times(1)).searchMovie(title, true, null, null, page, null, null);
    }

    @Test
    void getMovieDirectors_ShouldReturnDirectors_WhenApiResponseIsValid() {
        // Given
        String movieId = String.valueOf(MOVIE_ID_1);
        MovieCredits200Response mockResponse = new MovieCredits200Response();
        mockResponse.setCrew(List.of(
                new MovieCredits200ResponseCrewInner().job("Director").name(MOVIE_DIRECTOR_1),
                new MovieCredits200ResponseCrewInner().job("Producer").name("Emma Thomas")
        ));

        when(defaultApi.movieCredits(Integer.parseInt(movieId), null))
                .thenReturn(mockResponse);

        // When
        List<String> directors = client.getMovieDirectors(movieId);

        // Then
        assertThat(directors).containsExactly(MOVIE_DIRECTOR_1);

        verify(defaultApi, times(1)).movieCredits(Integer.parseInt(movieId), null);
    }

    @Test
    void getMovieDirectors_ShouldReturnNotFound_WhenNoDirectorsArePresent() {
        // Given
        String movieId = String.valueOf(MOVIE_ID_1);
        MovieCredits200Response mockResponse = new MovieCredits200Response();
        mockResponse.setCrew(List.of(
                new MovieCredits200ResponseCrewInner().job("Producer").name("Emma Thomas")
        ));

        when(defaultApi.movieCredits(Integer.parseInt(movieId), null))
                .thenReturn(mockResponse);

        // When
        List<String> directors = client.getMovieDirectors(movieId);

        // Then
        assertThat(directors).containsExactly(MovieApiClient.DIRECTOR_NOT_FOUND);

        verify(defaultApi, times(1)).movieCredits(Integer.parseInt(movieId), null);
    }

    @Test
    void getMovieDirectors_ShouldReturnNotFound_WhenApiReturnsNull() {
        // Given
        String movieId = String.valueOf(MOVIE_ID_1);

        when(defaultApi.movieCredits(Integer.parseInt(movieId), null))
                .thenReturn(null);

        // When
        List<String> directors = client.getMovieDirectors(movieId);

        // Then
        assertThat(directors).containsExactly(MovieApiClient.DIRECTOR_NOT_FOUND);

        verify(defaultApi, times(1)).movieCredits(Integer.parseInt(movieId), null);
    }
}
