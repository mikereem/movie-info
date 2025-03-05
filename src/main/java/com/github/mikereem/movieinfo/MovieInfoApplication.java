package com.github.mikereem.movieinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieInfoApplication.class, args);
    }

}
