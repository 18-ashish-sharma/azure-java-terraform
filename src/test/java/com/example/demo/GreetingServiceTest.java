package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    @Test
    void greetsByName() {
        assertThat(service.greet("Kerala")).isEqualTo("Hello, Kerala!");
    }

    @Test
    void fallsBackToWorldWhenNameIsMissing() {
        assertThat(service.greet(null)).isEqualTo("Hello, world!");
        assertThat(service.greet("  ")).isEqualTo("Hello, world!");
    }
}
