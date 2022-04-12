package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.PowerOfAttorneyRequest;
import com.onedoorway.project.dto.UpdatePowerOfAttorneyRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.PowerOFAttorneyRepository;
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
public class PowerOfAttorneyControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private PowerOFAttorneyRepository powerOFAttorneyRepository;

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
        powerOFAttorneyRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create power of attorney")
    void testCreatePowerOfAttorneySuccess() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);

        PowerOfAttorneyRequest request =
                PowerOfAttorneyRequest.builder()
                        .id(1L)
                        .clientId(client.getId())
                        .type("content")
                        .name("Jessica")
                        .phone("0000000000")
                        .email("jessica1221@email.com")
                        .address1("18th street,New Jersey")
                        .address2("California,USA")
                        .city("New Jersey")
                        .state("California")
                        .postCode("6806")
                        .lastUpdatedBy("swarna")
                        .build();
        // When
        mockMvc.perform(
                        post("/power-of-attorney/create")
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
    @DisplayName("POST /create power of attorney without fields")
    void testPowerOfAttorneySuccess_WithoutFields() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);

        PowerOfAttorneyRequest request =
                PowerOfAttorneyRequest.builder()
                        .id(1L)
                        .type("small")
                        .clientId(client.getId())
                        .name("Jessica")
                        .state("California")
                        .postCode("6806")
                        .lastUpdatedBy("swarna")
                        .build();
        // When
        mockMvc.perform(
                        post("/power-of-attorney/create")
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
    @DisplayName("POST /create power of attorney failure")
    void testPowerOfAttorneyFailure() {
        // Given
        PowerOfAttorneyRequest request =
                PowerOfAttorneyRequest.builder()
                        .type("small")
                        .name("Ali")
                        .state("California")
                        .postCode("6806")
                        .lastUpdatedBy("swarna")
                        .build();

        // When
        mockMvc.perform(
                        post("/power-of-attorney/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / list power of attorney")
    void testListPowerOfAttorney_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();

        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";

        String postcode = "1234";
        String lastUpdatedBy = "sam";
        client = clientRepository.save(client);
        PowerOfAttorney powerOfAttorney =
                PowerOfAttorney.builder()
                        .client(client)
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();
        powerOFAttorneyRepository.save(powerOfAttorney);

        // When
        mockMvc.perform(
                        get("/power-of-attorney/list/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(powerOfAttorney.getId()))
                .andExpect(
                        jsonPath("$.[0].clientName").value(powerOfAttorney.getClient().getName()))
                .andExpect(jsonPath("$.[0].name").value(powerOfAttorney.getName()))
                .andExpect(jsonPath("$[0].type").value(powerOfAttorney.getType()))
                .andExpect(jsonPath("$.[0].phone").value(powerOfAttorney.getPhone()))
                .andExpect(jsonPath("$.[0].email").value(powerOfAttorney.getEmail()))
                .andExpect(jsonPath("$.[0].address1").value(powerOfAttorney.getAddress1()))
                .andExpect(jsonPath("$.[0].address2").value(powerOfAttorney.getAddress2()))
                .andExpect(jsonPath("$.[0].city").value(powerOfAttorney.getCity()))
                .andExpect(jsonPath("$.[0].state").value(powerOfAttorney.getState()))
                .andExpect(jsonPath("$.[0].postCode").value(powerOfAttorney.getPostCode()))
                .andExpect(jsonPath("$.[0].deleted").value(powerOfAttorney.getDeleted()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedBy").value(powerOfAttorney.getLastUpdatedBy()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update Power Of Attorney success")
    void testUpdatePowerOfAttorney_Success() {
        // Given
        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";
        String postcode = "1234";
        String lastUpdatedBy = "sam";
        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        PowerOfAttorney powerOfAttorney =
                PowerOfAttorney.builder()
                        .client(client)
                        .type("medium")
                        .name("rai")
                        .phone("123")
                        .email("rai@gmail.com")
                        .address1("116 mesa vista drive")
                        .address2("326 harvest moon")
                        .city("hamilton")
                        .state("ontario")
                        .postCode("4582")
                        .deleted(false)
                        .lastUpdatedBy("cody")
                        .build();
        powerOFAttorneyRepository.save(powerOfAttorney);

        UpdatePowerOfAttorneyRequest request =
                UpdatePowerOfAttorneyRequest.builder()
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/power-of-attorney/update/{id}", powerOfAttorney.getId())
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
    @DisplayName("PUT /update power of attorney failure")
    void testUpdatePowerOfAttorney_Failure() {
        // Given
        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";
        String postcode = "1234";
        String lastUpdatedBy = "sam";
        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        PowerOfAttorney powerOfAttorney =
                PowerOfAttorney.builder()
                        .client(client)
                        .type("medium")
                        .name("rai")
                        .phone("123")
                        .email("rai@gmail.com")
                        .address1("116 mesa vista drive")
                        .address2("326 harvest moon")
                        .city("hamilton")
                        .state("ontario")
                        .postCode("4582")
                        .deleted(false)
                        .lastUpdatedBy("cody")
                        .build();
        powerOFAttorneyRepository.save(powerOfAttorney);

        UpdatePowerOfAttorneyRequest request =
                UpdatePowerOfAttorneyRequest.builder()
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/power-of-attorney/update/{id}", 400)
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
