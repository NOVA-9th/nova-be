package com.nova.nova_server.domain.keyword.repository;

import com.nova.nova_server.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByNameIn(List<String> names);
    Optional<Keyword> findByName(String name);
}
