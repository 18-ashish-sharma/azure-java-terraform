package com.onedoorway.project.controller;

import static com.onedoorway.project.model.LookupType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.LookupRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.util.Collections;
import java.util.HashSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class LookupControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private LookupRepository lookupRepository;

    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .email("test@test.com")
                        .password("password")
                        .firstName("First")
                        .lastName("Last")
                        .roles(
                                new HashSet<>(
                                        Collections.singletonList(
                                                Role.builder().name("USER").build())))
                        .build();
        userRepository.save(user);

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
    public void tearDown() {
        userRepository.deleteAll();
        lookupRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all houseFactors")
    void testListHouseFactors() {
        // Given
        Lookup lookup =
                Lookup.builder().id(1).name("test").lookupType(INCIDENT_HOUSE_ASSET_FACTOR).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/house-factors")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all classifications")
    void testListClassifications() {
        // Given
        Lookup lookup =
                Lookup.builder().id(1).name("test").lookupType(INCIDENT_CLASSIFICATION).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/classifications")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all environment factors")
    void testListEnvironmentFactors() {
        // Given
        Lookup lookup =
                Lookup.builder().id(1).name("test").lookupType(INCIDENT_ENVIRONMENT_FACTOR).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/environment-factors")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all person factors")
    void testListPersonFactors() {
        // Given
        Lookup lookup =
                Lookup.builder().id(1).name("test").lookupType(INCIDENT_PERSON_FACTOR).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/person-factors")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all categories")
    void testListCategories() {
        // Given
        Lookup lookup = Lookup.builder().id(1).name("test").lookupType(INCIDENT_CATEGORY).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/categories")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all types")
    void testListTypes() {
        // Given
        Lookup lookup = Lookup.builder().id(1).name("test").lookupType(INCIDENT_TYPE).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/types")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all reports")
    void testListReports() {
        // Given
        Lookup lookup = Lookup.builder().id(1).name("test").lookupType(REPORTS).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/reports")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all nightly tasks")
    void testListNightlyTasks() {
        // Given
        Lookup lookup = Lookup.builder().id(1).name("test").lookupType(NIGHTLY_TASKS).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/nightly-tasks")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list all miscellaneous notes")
    void testListMiscellaneousNotes() {
        // Given
        Lookup lookup = Lookup.builder().id(1).name("test").lookupType(MISCELLANEOUS_NOTES).build();
        lookup = lookupRepository.save(lookup);

        // When
        mockMvc.perform(
                        get("/lookup/list-note-categories")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(lookup.getId()))
                .andExpect(jsonPath("$.[0].name").value(lookup.getName()));
    }
}
