package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onedoorway.project.dto.ToggleReportRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientReportRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.LookupRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ClientReportControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private LookupRepository lookupRepository;

    @Autowired private ClientReportRepository clientReportRepository;

    private Client testClient;

    private Lookup testLookup;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().id(1).name("USER").build())))
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
        clientReportRepository.deleteAll();
        lookupRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get report")
    void testGetClientReport_Success() {
        // Given
        long id = 1;
        Lookup lookup =
                Lookup.builder().id(id).name("tests").lookupType(LookupType.REPORTS).build();
        lookup = lookupRepository.save(lookup);

        Client client = Client.builder().id(id).name("name").build();
        client = clientRepository.save(client);

        ClientReport clientReport =
                ClientReport.builder().client(client).lookup(lookup).toggle(true).build();
        clientReport = clientReportRepository.save(clientReport);

        // When
        mockMvc.perform(
                        get("/clientReport/get/{clientId}", clientReport.getClient().getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].clientId").value(clientReport.getClient().getId()))
                .andExpect(jsonPath("$.[0].lookupId").value(clientReport.getLookup().getId()))
                .andExpect(jsonPath("$.[0].toggle").value(true));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /when Toggle is ON")
    void testWhenToggle_isON() {
        // Given
        testClient = Client.builder().name("check").build();
        clientRepository.save(testClient);
        Lookup lookup = Lookup.builder().name("tests").build();
        lookupRepository.save(lookup);

        ToggleReportRequest request =
                ToggleReportRequest.builder()
                        .clientId(testClient.getId())
                        .lookupId(lookup.getId())
                        .toggle(true)
                        .build();

        // When
        mockMvc.perform(
                        post("/clientReport/toggle-report")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /when Toggle is OFF")
    void testWhenToggle_isOFF() {
        // Given
        testClient = Client.builder().name("testing").build();
        clientRepository.save(testClient);

        Lookup lookup = Lookup.builder().name("check").build();
        lookupRepository.save(lookup);

        ToggleReportRequest request =
                ToggleReportRequest.builder()
                        .clientId(testClient.getId())
                        .lookupId(lookup.getId())
                        .toggle(false)
                        .build();

        // When
        mockMvc.perform(
                        post("/clientReport/toggle-report")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    static String asJsonString(final Object obj) {
        try {
            return JsonMapper.builder()
                    .serializationInclusion(JsonInclude.Include.NON_NULL)
                    .build()
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
