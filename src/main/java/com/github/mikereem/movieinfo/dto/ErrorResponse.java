package com.github.mikereem.movieinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Error response")
public class ErrorResponse {
    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "Invalid API response")
    private String message;
}
