package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedoorway.project.dto.RegisterRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.RoleRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.util.Collections;
import java.util.HashSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(
                                new HashSet<>(
                                        Collections.singletonList(
                                                Role.builder().id(1).name("ADMIN").build())))
                        .build();
        userRepository.save(user);

        Role userRole = Role.builder().name("USER").build();
        roleRepository.save(userRole);

        ODWUserDetails basicUser = new ODWUserDetails(user);
        when(mockJwtUtil.extractUsername(anyString())).thenReturn("test@test.com");
        when(mockJwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(mockUserDetailsService.loadUserByUsername(anyString())).thenReturn(basicUser);
        this.mockMvc =
                webAppContextSetup(this.wac)
                        .addFilters(new JwtRequestFilter(mockUserDetailsService, mockJwtUtil))
                        .build();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /register")
    void testRegisterSuccess() {
        // Given
        String email = "test-unique@test.com";
        String password = "testme";
        String firstName = "First";
        String lastName = "Last";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .password(password)
                        .phone(phone)
                        .mobile(mobile)
                        .build();

        // When
        mockMvc.perform(
                        post("/register")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(registerRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /register password invalid")
    void testRegisterFail_password_invalid() {
        // Given
        String email = "test@test.com";
        String password = "test"; // password less than 5 characters
        String firstName = "First";
        String lastName = "Last";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email(email)
                        .password(password)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(phone)
                        .mobile(mobile)
                        .build();

        // When
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer dummy")
                                .content(asJsonString(registerRequest)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /register email invalid")
    void testRegisterFail_email_invalid() {
        // Given
        String email = "invalid"; // invalid email
        String password = "testme";
        String firstName = "First";
        String lastName = "Last";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .password(password)
                        .phone(phone)
                        .mobile(mobile)
                        .build();

        // When
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer dummy")
                                .content(asJsonString(registerRequest)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /register firstname invalid")
    void testRegisterFail_firstName_invalid() {
        // Given
        String email = "test@test.com";
        String password = "testme";
        String firstName = ""; // invalid firstname
        String lastName = "Last";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .password(password)
                        .phone(phone)
                        .mobile(mobile)
                        .build();

        // When
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer dummy")
                                .content(asJsonString(registerRequest)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /register lastname invalid")
    void testRegisterFail_lastName_invalid() {
        // Given
        String email = "test@test.com";
        String password = "testme";
        String firstName = "First";
        String lastName = ""; // invalid lastname
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .password(password)
                        .phone(phone)
                        .mobile(mobile)
                        .build();

        // When
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer dummy")
                                .content(asJsonString(registerRequest)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /register without fields")
    void testRegisterSuccess_WithoutFields() {
        // Given
        String email = "test-uni@test.com";
        String password = "testme";
        String firstName = "First";
        String lastName = "Last";
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .password(password)
                        .build();

        // When
        mockMvc.perform(
                        post("/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer dummy")
                                .content(asJsonString(registerRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
