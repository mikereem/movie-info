package com.github.mikereem.movieinfo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String movieTitle;

    @Column(nullable = false)
    private String apiName;

    @Column(nullable = false)
    private int page;

    @Column(nullable = false)
    private LocalDateTime requestTime;
}
