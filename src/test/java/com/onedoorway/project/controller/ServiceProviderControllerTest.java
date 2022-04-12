package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.ServiceProviderRequest;
import com.onedoorway.project.dto.UpdateServiceProviderRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.ServiceProviderRepository;
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
public class ServiceProviderControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private ServiceProviderRepository serviceProviderRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private ClientRepository clientRepository;

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
        serviceProviderRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create service provider")
    void testCreateServiceProviderSuccess() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);

        ServiceProviderRequest request =
                ServiceProviderRequest.builder()
                        .clientId(client.getId())
                        .name("name")
                        .service("service")
                        .phone("1234444777")
                        .email("jake@email.com")
                        .lastUpdatedBy("sam")
                        .build();

        // When
        mockMvc.perform(
                        post("/service-provider/service-provider/create")
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
    @DisplayName("GET / list serviceProvider")
    void testGetServiceProvider_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        ServiceProvider serviceProvider =
                ServiceProvider.builder()
                        .client(client)
                        .name("joy")
                        .service("service")
                        .deleted(false)
                        .phone("1234444777")
                        .email("jake@email.com")
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();
        serviceProviderRepository.save(serviceProvider);

        // When
        mockMvc.perform(
                        get("/service-provider/list/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(serviceProvider.getId()))
                .andExpect(
                        jsonPath("$.[0].clientName").value(serviceProvider.getClient().getName()))
                .andExpect(jsonPath("$[0].name").value(serviceProvider.getName()))
                .andExpect(jsonPath("$.[0].service").value(serviceProvider.getService()))
                .andExpect(jsonPath("$.[0].deleted").value(serviceProvider.getDeleted()))
                .andExpect(jsonPath("$.[0].email").value(serviceProvider.getEmail()))
                .andExpect(jsonPath("$.[0].phone").value(serviceProvider.getPhone()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedBy").value(serviceProvider.getLastUpdatedBy()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update Service provider success")
    void testUpdateServiceProvider_Success() {
        // Given
        String name = "joy";
        String service = "Telephone";
        String lastUpdatedBy = "jimmy";
        Client client = Client.builder().id(1).name("client").build();
        String email = "maya212@gmail.com";
        String phone = "7736312992";
        client = clientRepository.save(client);
        ServiceProvider serviceProvider =
                ServiceProvider.builder()
                        .client(client)
                        .name("joy")
                        .service("service")
                        .deleted(false)
                        .lastUpdatedBy("lastUpdatedBy")
                        .email("")
                        .phone("")
                        .build();
        serviceProviderRepository.save(serviceProvider);
        UpdateServiceProviderRequest request =
                UpdateServiceProviderRequest.builder()
                        .name(name)
                        .service(service)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .phone(phone)
                        .email(email)
                        .build();
        // When
        mockMvc.perform(
                        put("/service-provider/update/{id}", serviceProvider.getId())
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
    @DisplayName("PUT /update Service provider Failure")
    void testUpdateServiceProvider_Failure() {
        // Given
        String name = "joy";
        String service = "Telephone";
        String lastUpdatedBy = "jimmy";
        Client client = Client.builder().id(1).name("client").build();
        String email = "maya212@gmail.com";
        String phone = "7736312992";
        client = clientRepository.save(client);
        ServiceProvider serviceProvider =
                ServiceProvider.builder()
                        .client(client)
                        .name("joy")
                        .service("service")
                        .deleted(false)
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();
        serviceProviderRepository.save(serviceProvider);
        UpdateServiceProviderRequest request =
                UpdateServiceProviderRequest.builder()
                        .name(name)
                        .service(service)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
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
