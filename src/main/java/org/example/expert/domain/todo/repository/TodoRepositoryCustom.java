package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoRepositoryCustom {

    Page<Todo> findAllByWeatherAndModifiedAtRange(
            String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );

    Page<TodoSearchResponse> findAllByTitleNicknameWithCreatedAtRange(
            String title, String nickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    );

    Optional<Todo> findByIdWithUser(Long todoId);
}
