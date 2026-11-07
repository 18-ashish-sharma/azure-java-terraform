package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public String greet(String name) {
        String who = (name == null || name.isBlank()) ? "world" : name.strip();
        return "Hello, " + who + "!";
    }
}
