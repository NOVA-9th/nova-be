package com.nova.nova_server.domain.interest.repository;

import com.nova.nova_server.domain.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
}
