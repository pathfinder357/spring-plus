package org.example.expert.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {
    ADMIN(Authority.ADMIN),
    USER(Authority.USER);

    private final String userRole;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UserRole"));
    }

    @Override
    public String getAuthority() {
        return userRole;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
