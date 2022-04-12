package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.ClientAllowancesRequest;
import com.onedoorway.project.dto.UpdateClientAllowancesRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientAllowancesRepository;
import com.onedoorway.project.repository.ClientRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ClientAllowancesControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;
    @Autowired private ClientRepository clientRepository;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private ClientAllowancesRepository clientAllowancesRepository;

    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(
                                new HashSet<>(
                                        Collections.singletonList(
                                                Role.builder().id(1).name("USER").build())))
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
        clientAllowancesRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create clientAllowances")
    void testCreateClientAllowances_Success() {
        // Given
        Client client = Client.builder().id(1).build();
        client = clientRepository.save(client);
        ClientAllowancesRequest request =
                ClientAllowancesRequest.builder()
                        .clientId(client.getId())
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        // When
        mockMvc.perform(
                        post("/client-allowances/create")
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
    @DisplayName("POST /create client when fields are blank")
    void testCreateClientAllowances_WithoutField() {
        // Given
        Client client = Client.builder().id(1).build();
        client = clientRepository.save(client);
        ClientAllowancesRequest request =
                ClientAllowancesRequest.builder()
                        .clientId(client.getId())
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .build();

        // When
        mockMvc.perform(
                        post("/client-allowances/create")
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
    @DisplayName("GET / list clientAllowances")
    void testGetClientAllowances_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        ClientAllowances clientAllowances =
                ClientAllowances.builder()
                        .client(client)
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        clientAllowancesRepository.save(clientAllowances);

        // When
        mockMvc.perform(
                        get("/client-allowances/list/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].cappedKMs").value(clientAllowances.getCappedKMs()))
                .andExpect(
                        jsonPath("$.[0].concessionCard")
                                .value(clientAllowances.getConcessionCard()))
                .andExpect(jsonPath("$[0].kms").value(clientAllowances.getKms()))
                .andExpect(jsonPath("$.[0].grocerySpend").value(clientAllowances.getGrocerySpend()))
                .andExpect(
                        jsonPath("$.[0].budgetlyCardNo")
                                .value(clientAllowances.getBudgetlyCardNo()))
                .andExpect(jsonPath("$.[0].deleted").value(clientAllowances.getDeleted()))
                .andExpect(
                        jsonPath("$.[0].lastUploadedBy")
                                .value(clientAllowances.getLastUploadedBy()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update Client Allowances success")
    void testUpdateClientAllowances_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        ClientAllowances clientAllowances =
                ClientAllowances.builder()
                        .client(client)
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        clientAllowancesRepository.save(clientAllowances);
        UpdateClientAllowancesRequest request =
                UpdateClientAllowancesRequest.builder()
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        // When
        mockMvc.perform(
                        put("/client-allowances/update/{id}", clientAllowances.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update Client Allowances Failure")
    void testUpdateClientAllowances_Failure() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        ClientAllowances clientAllowances =
                ClientAllowances.builder()
                        .client(client)
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        clientAllowancesRepository.save(clientAllowances);
        UpdateClientAllowancesRequest request =
                UpdateClientAllowancesRequest.builder()
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        // When
        mockMvc.perform(
                        put("/service-provider/update/{id}", 300)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
