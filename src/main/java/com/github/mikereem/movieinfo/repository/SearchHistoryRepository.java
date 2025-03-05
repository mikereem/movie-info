package com.github.mikereem.movieinfo.repository;

import com.github.mikereem.movieinfo.entity.SearchHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends CrudRepository<SearchHistory, Long> {
}
