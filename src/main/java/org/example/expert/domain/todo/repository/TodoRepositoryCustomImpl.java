package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.comment.entity.QComment.*;
import static org.example.expert.domain.manager.entity.QManager.*;
import static org.example.expert.domain.todo.entity.QTodo.*;
import static org.example.expert.domain.user.entity.QUser.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 특정 일정을 유저와 함께 조회
    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo findTodo = queryFactory
                .select(todo)
                .from(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();
        return Optional.ofNullable(findTodo);
    }

    // 제목, 닉네임, 생성일짜로 일정 검색

    @Override
    public Page<TodoSearchResponse> findAllByTitleNicknameWithCreatedAtRange(
            String title, String nickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    ) {
            QTodo t = QTodo.todo;
            QUser u = QUser.user;
            QManager m = QManager.manager;
            QComment c = QComment.comment;

            List<TodoSearchResponse> resultList = queryFactory
                    .select(Projections.constructor(TodoSearchResponse.class,
                                    t.title,
                                    m.id.countDistinct(),
                                    c.id.countDistinct()
                            )
                    )
                    .from(t)
                    .leftJoin(t.managers, m)
                    .leftJoin(m.user, u)
                    .leftJoin(t.comments, c)
                    .where(
                            titleContains(title),
                            nicknameContains(nickname),
                            dateBetween(startDate, endDate)
                    )
                    .groupBy(t.id)
                    .orderBy(t.createdAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            Long totalCount = queryFactory
                    .select(t.count())
                    .from(t)
                    .leftJoin(t.managers, m)
                    .leftJoin(m.user, u)
                    .where(
                            titleContains(title),
                            nicknameContains(nickname),
                            dateBetween(startDate, endDate)
                    )
                    .fetchOne();
            return new PageImpl<>(resultList, pageable, totalCount != null ? totalCount : 0L);
    }

    // 날씨와 날짜 범위로 일정 검색
    @Override
    public Page<Todo> findAllByWeatherAndModifiedAtRange(
            String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable
    ) {
        QTodo t = QTodo.todo;
        QUser u = QUser.user;

        List<Todo> todos = queryFactory
                .selectFrom(t)
                .leftJoin(t.user, u).fetchJoin()
                .where(
                        weatherEq(weather),
                        dateBetween(startDate, endDate)
                )
                .orderBy(t.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(t.count())
                .from(t)
                .where(
                        weatherEq(weather),
                        dateBetween(startDate, endDate)
                )
                .fetchOne();

        return new PageImpl<>(todos, pageable, totalCount != null ? totalCount : 0L);
    }

    // 코드 가독성 위해 조건 별도 메서드 분리
    private BooleanExpression weatherEq(String weather) {
        return (weather != null && !weather.isEmpty()) ? todo.weather.eq(weather) : null;
    }

    private BooleanExpression titleContains(String title) {
        return (title != null && !title.isEmpty()) ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return (nickname != null && !nickname.isEmpty()) ? manager.user.nickname.containsIgnoreCase(nickname) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return todo.createdAt.between(startDate, endDate);
        } else if (startDate != null) {
            return todo.createdAt.goe(startDate);
        } else if (endDate != null) {
            return todo.createdAt.loe(endDate);
        }
        return null;
    }

}