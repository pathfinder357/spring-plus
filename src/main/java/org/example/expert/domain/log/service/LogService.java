package org.example.expert.domain.log.service;

import java.time.LocalDateTime;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String message) {
        Log log = Log.builder()
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        logRepository.save(log);
    }
}
