package com.github.mikereem.movieinfo.event;

import com.github.mikereem.movieinfo.dto.SearchParams;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SearchEvent extends ApplicationEvent {

    private final SearchParams searchParams;

    public SearchEvent(Object source, SearchParams searchParams) {
        super(source);
        this.searchParams = searchParams;
    }
}
