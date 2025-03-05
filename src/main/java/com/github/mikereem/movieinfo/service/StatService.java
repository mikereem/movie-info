package com.github.mikereem.movieinfo.service;

import com.github.mikereem.movieinfo.dto.SearchParams;
import com.github.mikereem.movieinfo.entity.SearchHistory;
import com.github.mikereem.movieinfo.repository.SearchHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class StatService {

    private final SearchHistoryRepository searchHistoryRepository;

    public StatService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public void storeSearchHistory(SearchParams searchParams) {
        log.info("Storing the search history {}", searchParams);
        SearchHistory searchHistory = SearchHistory.builder()
                .movieTitle(searchParams.getMovieTitle())
                .apiName(searchParams.getApiName())
                .page(searchParams.getPage())
                .requestTime(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(searchHistory);
    }
}
