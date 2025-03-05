package com.github.mikereem.movieinfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidApiResponseException extends RuntimeException {
    public InvalidApiResponseException() {
        super("External API cannot answer with the current request parameters");
    }
}
