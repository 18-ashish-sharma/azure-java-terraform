package com.onedoorway.project;

import java.time.Instant;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Context {
    public Instant now() {
        return Instant.now();
    }

    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Objects.isNull(authentication) ? null : authentication.getName();
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null
                && auth.getAuthorities().stream()
                        .anyMatch(
                                a ->
                                        a.getAuthority().equals("ADMIN")
                                                || a.getAuthority().equals("HOUSE ADMIN")));
    }
}
