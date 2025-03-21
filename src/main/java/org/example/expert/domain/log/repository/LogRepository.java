package org.example.expert.domain.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.expert.domain.log.entity.Log;
public interface LogRepository extends JpaRepository<Log, Long> {
}

