package com.github.mikereem.movieinfo.config;

import com.github.mikereem.movieinfo.client.omdb.ApiClient;
import com.github.mikereem.movieinfo.client.omdb.api.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MovieApiClientConfig {

    @Value("${movie-api.omdb-base-url}")
    private String omdbApiBaseUrl;

    @Value("${movie-api.tmdb-base-url}")
    private String tmdbApiBaseUrl;
    @Value("${movie-api.tmdb-key}")
    private String tmdbApiKey;

    @Bean
    public DefaultApi getOmdbDefaultApi() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(omdbApiBaseUrl);
        return new DefaultApi(apiClient);
    }

    @Bean
    public com.github.mikereem.movieinfo.client.tmdb.api.DefaultApi getTmdbDefaultApi() {
        com.github.mikereem.movieinfo.client.tmdb.ApiClient apiClient = new com.github.mikereem.movieinfo.client.tmdb.ApiClient();
        apiClient.setBasePath(tmdbApiBaseUrl);
        apiClient.addDefaultHeader("Authorization", "Bearer " + tmdbApiKey);
        return new com.github.mikereem.movieinfo.client.tmdb.api.DefaultApi(apiClient);
    }
}
