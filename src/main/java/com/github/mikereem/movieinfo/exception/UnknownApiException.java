package com.github.mikereem.movieinfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownApiException extends RuntimeException {
    public UnknownApiException(String apiName) {
        super("Unknown apiName: " + apiName);
    }
}
