package com.github.mikereem.movieinfo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed information about a movie")
public class Movie implements Serializable {
    @JsonIgnore
    private String movieId;
    @Schema(description = "Movie title", example = "Inception")
    private String title;
    @Schema(description = "Release year", example = "2010")
    private String year;
    @Schema(description = "List of directors", example = "[\"Christopher Nolan\"]")
    private List<String> directors;
}
