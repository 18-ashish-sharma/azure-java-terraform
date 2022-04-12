package com.onedoorway.project;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class FrozenContext extends Context {
    public final Instant nowValue = Instant.now();

    @Override
    public Instant now() {
        return nowValue;
    }

    @Override
    public String currentUser() {
        return "test@test.com";
    }

    @Override
    public boolean isAdmin() {
        return true;
    }
}
