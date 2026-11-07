package com.example.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HelloController.class)
@Import(GreetingService.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void returnsGreetingAsJson() throws Exception {
        mvc.perform(get("/api/hello").param("name", "panel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, panel!"));
    }

    @Test
    void defaultsToWorld() throws Exception {
        mvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, world!"));
    }
}
