package com.github.mikereem.movieinfo.service;

import com.github.mikereem.movieinfo.dto.SearchParams;
import com.github.mikereem.movieinfo.entity.SearchHistory;
import com.github.mikereem.movieinfo.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StatServiceTest {

    @InjectMocks
    private StatService statService;

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void storeSearchHistory_ShouldStoreCorrectData() {
        // Given
        SearchParams searchParams = new SearchParams(null, "omdb", 2);

        // When
        statService.storeSearchHistory(searchParams);

        // Then
        ArgumentCaptor<SearchHistory> captor = ArgumentCaptor.forClass(SearchHistory.class);
        verify(searchHistoryRepository, times(1)).save(captor.capture());

        SearchHistory savedHistory = captor.getValue();

        assertThat(savedHistory.getMovieTitle()).isEqualTo(null);
        assertThat(savedHistory.getApiName()).isEqualTo("omdb");
        assertThat(savedHistory.getPage()).isEqualTo(2);
        assertThat(savedHistory.getRequestTime()).isNotNull();
        assertThat(savedHistory.getRequestTime()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
