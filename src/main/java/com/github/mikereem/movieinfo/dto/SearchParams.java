package com.github.mikereem.movieinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@ToString
public class SearchParams implements Serializable {
    private final String movieTitle;
    private final String apiName;
    private final int page;
}
