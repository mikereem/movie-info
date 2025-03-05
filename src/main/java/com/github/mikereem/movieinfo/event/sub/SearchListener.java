package com.github.mikereem.movieinfo.event.sub;

import com.github.mikereem.movieinfo.event.SearchEvent;
import com.github.mikereem.movieinfo.service.StatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SearchListener {

    private final StatService statService;

    @EventListener
    @Async
    public void storeSearch(SearchEvent event) {
        log.info("Search received: {}", event.getSearchParams());
        try {
            statService.storeSearchHistory(event.getSearchParams());
        } catch (Exception e) {
            log.error("Failed to store search event", e);
        }
    }
}
