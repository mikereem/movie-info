package com.github.mikereem.movieinfo.event.pub;

import com.github.mikereem.movieinfo.dto.SearchParams;
import com.github.mikereem.movieinfo.event.SearchEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SearchPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(SearchParams searchParams) {
        try {
            applicationEventPublisher.publishEvent(new SearchEvent(this, searchParams));
        } catch (Exception e) {
            log.error("Failed to publish search", e);
        }
    }
}
