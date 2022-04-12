package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedoorway.project.dto.LoginRequest;
import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.services.HouseService;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.services.UserService;
import com.onedoorway.project.util.JwtUtil;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockBean private AuthenticationManager mockAuthenticationManager;

    @MockBean private ODWUserDetailsService mockUserDetailsService;

    @MockBean private HouseService mockHouseService;

    @MockBean private UserService mockUserService;

    @MockBean private JwtUtil mockJwtUtil;

    @SneakyThrows
    @Test
    @DisplayName("Success POST /login")
    void testLoginSuccess() {
        String email = "test@test.com";
        String password = "secret";

        LoginRequest loginRequest = LoginRequest.builder().email(email).password(password).build();

        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        ODWUserDetails mockUserDetails = mock(ODWUserDetails.class);
        when(mockUserDetails.getFirstName()).thenReturn("firstName");
        when(mockUserDetails.getLastName()).thenReturn("lastName");
        when(mockUserDetails.getUsername()).thenReturn(email);
        when(mockUserService.isUserDeleted(mockUserDetails.getUsername())).thenReturn(false);
        when(mockUserDetailsService.loadUserByUsername("test@test.com"))
                .thenReturn(mockUserDetails);
        when(mockJwtUtil.generateToken(any(UserDetails.class))).thenReturn("token");
        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(loginRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt", is("token")))
                .andExpect(jsonPath("$.firstName", is("firstName")))
                .andExpect(jsonPath("$.lastName", is("lastName")))
                .andExpect(jsonPath("$.houseCode", is(List.of())));
    }

    @SneakyThrows
    @Test
    @DisplayName("Failure wrong password POST /login")
    void testLoginFailure_WrongPass() {
        String email = "test@test.com";
        String password = "wrongPass";
        String houseCode = "101";

        LoginRequest loginRequest = LoginRequest.builder().email(email).password(password).build();

        when(mockAuthenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);
        when(mockHouseService.isUserInHouse(email, houseCode)).thenReturn(true);
        when(mockUserService.isUserDeleted(email)).thenReturn(true);
        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(loginRequest)))
                // Then
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
