package com.github.mikereem.movieinfo.service.api.impl;

import com.github.mikereem.movieinfo.client.omdb.api.DefaultApi;
import com.github.mikereem.movieinfo.client.omdb.model.CombinedResult;
import com.github.mikereem.movieinfo.client.omdb.model.CombinedResultSearchInner;
import com.github.mikereem.movieinfo.dto.PagedSearchResult;
import com.github.mikereem.movieinfo.exception.InvalidApiResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class OmdbMovieApiClientTest {

    private static final String API_KEY = "TEST_API_KEY";
    private static final String MOVIE_TITLE_1 = "Back to the Future";
    private static final String MOVIE_ID_1 = "id1";
    private static final String MOVIE_TITLE_2 = "Back to the Future Part II";
    private static final String MOVIE_ID_2 = "id2";
    private static final String MOVIE_YEAR_1 = "1985";
    private static final String MOVIE_YEAR_2 = "1989";
    private static final String MOVIE_DIRECTOR_1 = "Robert Zemeckis";

    @InjectMocks
    private OmdbMovieApiClient client;

    @Mock
    private DefaultApi defaultApi;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(client, "apiKey", API_KEY);
    }

    @Test
    void getTotalPages_ShouldReturnCorrectPages_WhenValidNumberProvided() {
        assertThat(client.getTotalPages("100")).isEqualTo(10);  // 100 / 10 = 10
        assertThat(client.getTotalPages("101")).isEqualTo(11);  // 101 / 10 = 11 (round up)
        assertThat(client.getTotalPages("9")).isEqualTo(1);     // Less than PAGE_SIZE, should return 1 page
    }

    @Test
    void getTotalPages_ShouldReturnZero_WhenInputIsNull() {
        assertThat(client.getTotalPages(null)).isEqualTo(0);
    }

    @Test
    void getTotalPages_ShouldReturnZero_WhenInputIsEmptyString() {
        assertThat(client.getTotalPages("")).isEqualTo(0);
    }

    @Test
    void getTotalPages_ShouldReturnZero_WhenInputIsNonNumeric() {
        assertThat(client.getTotalPages("abc")).isEqualTo(0);
        assertThat(client.getTotalPages("10.5")).isEqualTo(0);
        assertThat(client.getTotalPages("100xyz")).isEqualTo(0);
    }

    @Test
    void isSuccessful_ShouldReturnTrue_WhenResponseIsValidTrue() {
        assertThat(client.isSuccessful("true")).isTrue();
    }

    @Test
    void isSuccessful_ShouldReturnTrue_WhenResponseIsValidTrueCaseInsensitive() {
        assertThat(client.isSuccessful("True")).isTrue();
    }

    @Test
    void isSuccessful_ShouldReturnFalse_WhenResponseIsValidFalse() {
        assertThat(client.isSuccessful("false")).isFalse();
    }

    @Test
    void isSuccessful_ShouldReturnFalse_WhenInputIsNull() {
        assertThat(client.isSuccessful(null)).isFalse();
    }

    @Test
    void isSuccessful_ShouldReturnFalse_WhenInputIsEmptyString() {
        assertThat(client.isSuccessful("")).isFalse();
    }

    @Test
    void isSuccessful_ShouldReturnFalse_WhenInputIsInvalidBoolean() {
        assertThat(client.isSuccessful("yes")).isFalse();
        assertThat(client.isSuccessful("1")).isFalse();
        assertThat(client.isSuccessful("TRUE ")).isFalse();
    }

    @Test
    void searchMovies_ShouldReturnMovies_WhenApiResponseIsValid() {
        // Given
        String title = MOVIE_TITLE_1;
        int page = 1;
        CombinedResult mockResponse = new CombinedResult();
        mockResponse.setResponse("True");
        mockResponse.setSearch(List.of(
                new CombinedResultSearchInner().imdbID(MOVIE_ID_1).title(MOVIE_TITLE_1).year(MOVIE_YEAR_1),
                new CombinedResultSearchInner().imdbID(MOVIE_ID_2).title(MOVIE_TITLE_2).year(MOVIE_YEAR_2)
        ));
        mockResponse.setTotalResults("2");

        when(defaultApi.getOMDbSearch("", API_KEY, null, null, title, null, null, null, null, null, page, null))
                .thenReturn(mockResponse);

        // When
        PagedSearchResult result = client.searchMovies(title, page);

        // Then
        assertThat(result.getMovies()).hasSize(2);
        assertThat(result.getMovies().get(0).getTitle()).isEqualTo(MOVIE_TITLE_1);
        assertThat(result.getMovies().get(0).getYear()).isEqualTo(MOVIE_YEAR_1);
        assertThat(result.getMovies().get(1).getTitle()).isEqualTo(MOVIE_TITLE_2);
        assertThat(result.getMovies().get(1).getYear()).isEqualTo(MOVIE_YEAR_2);
        assertThat(result.getTotalPages()).isEqualTo(1);

        verify(defaultApi, times(1)).getOMDbSearch("", API_KEY, null, null, title, null, null, null, null, null, page, null);
    }

    @Test
    void searchMovies_ShouldReturnEmptyResult_WhenApiReturnsNull() {
        // Given
        String title = MOVIE_TITLE_1;
        int page = 1;
        CombinedResult mockResponse = new CombinedResult();
        mockResponse.setResponse("True");
        mockResponse.setSearch(null);

        when(defaultApi.getOMDbSearch("", API_KEY, null, null, title, null, null, null, null, null, page, null))
                .thenReturn(mockResponse);

        // When
        PagedSearchResult result = client.searchMovies(title, page);

        // Then
        assertThat(result.getMovies()).isEmpty();
        assertThat(result.getTotalPages()).isEqualTo(0);

        verify(defaultApi, times(1)).getOMDbSearch("", API_KEY, null, null, title, null, null, null, null, null, page, null);
    }

    @Test
    void searchMovies_ShouldThrowException_WhenApiResponseIsInvalid() {
        // Given
        String title = MOVIE_TITLE_1;
        int page = 1;
        CombinedResult mockResponse = new CombinedResult();
        mockResponse.setResponse("False");

        when(defaultApi.getOMDbSearch("", API_KEY, null, null, title, null, null, null, null, null, page, null))
                .thenReturn(mockResponse);

        // When & Then
        assertThatThrownBy(() -> client.searchMovies(title, page))
                .isInstanceOf(InvalidApiResponseException.class);

        verify(defaultApi, times(1)).getOMDbSearch("", API_KEY, null, null, title, null, null, null, null, null, page, null);
    }

    @Test
    void getMovieDirectors_ShouldReturnDirectors_WhenApiResponseIsValid() {
        // Given
        String movieId = MOVIE_ID_1;
        CombinedResult mockResponse = new CombinedResult();
        mockResponse.setDirector(MOVIE_DIRECTOR_1);

        when(defaultApi.getOMDbSearch("", API_KEY, null, movieId, "", null, null, null, null, null, null, null))
                .thenReturn(mockResponse);

        // When
        List<String> directors = client.getMovieDirectors(movieId);

        // Then
        assertThat(directors).containsExactly(MOVIE_DIRECTOR_1);

        verify(defaultApi, times(1)).getOMDbSearch("", API_KEY, null, movieId, "", null, null, null, null, null, null, null);
    }

    @Test
    void getMovieDirectors_ShouldReturnDirectorNotFound_WhenDirectorIsNull() {
        // Given
        String movieId = MOVIE_ID_1;
        CombinedResult mockResponse = new CombinedResult();
        mockResponse.setDirector(null);

        when(defaultApi.getOMDbSearch("", API_KEY, null, movieId, "", null, null, null, null, null, null, null))
                .thenReturn(mockResponse);

        // When
        List<String> directors = client.getMovieDirectors(movieId);

        // Then
        assertThat(directors).containsExactly("N/A");

        verify(defaultApi, times(1)).getOMDbSearch("", API_KEY, null, movieId, "", null, null, null, null, null, null, null);
    }
}
