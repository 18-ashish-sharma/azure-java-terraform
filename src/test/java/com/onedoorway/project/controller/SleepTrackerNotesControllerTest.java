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
import com.onedoorway.project.dto.SleepTrackerNotesRequest;
import com.onedoorway.project.dto.UpdateSleepTrackerRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.SleepTrackerNotesRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
class SleepTrackerNotesControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private SleepTrackerNotesRepository sleepTrackerNotesRepository;

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
        sleepTrackerNotesRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create sleepTrackerNotes")
    void testCreateSleepTrackerNotesSuccess() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);

        SleepTrackerNotesRequest request =
                SleepTrackerNotesRequest.builder()
                        .clientId(client.getId())
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("Awake at 3:00am")
                        .fourthUpdatedBy("mary")
                        .fifthSlot("")
                        .fifthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Tom")
                        .seventhSlot("Awake at 6:00am")
                        .seventhUpdatedBy("mathew")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .build();

        // When
        mockMvc.perform(
                        post("/sleep-notes/create")
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
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create sleepTrackerNotes failure")
    void createSleepTrackerNotes_Success_WithoutFields() {
        // Given
        Client client = Client.builder().id(2L).name("client").build();
        client = clientRepository.save(client);

        SleepTrackerNotesRequest request =
                SleepTrackerNotesRequest.builder()
                        .clientId(client.getId())
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .fifthSlot("")
                        .fifthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Awake at 6:00am")
                        .seventhSlot("")
                        .seventhUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .build();

        // When
        mockMvc.perform(
                        post("/sleep-notes/create")
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
    @DisplayName("GET / get sleepTrackerNote")
    void testGetSleepTrackerNote_Success() {
        // Given
        long id = 1;
        LocalDate reportDate = LocalDate.now();
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        SleepTrackerNotes sleepTrackerNotes =
                SleepTrackerNotes.builder()
                        .client(client)
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Awake at 6:00am")
                        .seventhSlot("")
                        .seventhUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        sleepTrackerNotesRepository.save(sleepTrackerNotes);

        // When
        mockMvc.perform(
                        get("/sleep-notes/get/{clientId}/{reportDate}", client.getId(), reportDate)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(sleepTrackerNotes.getClient().getId()))
                .andExpect(jsonPath("$.clientName").value(sleepTrackerNotes.getClient().getName()))
                .andExpect(jsonPath("$.firstSlot").value(sleepTrackerNotes.getFirstSlot()))
                .andExpect(
                        jsonPath("$.firstUpdatedBy").value(sleepTrackerNotes.getFirstUpdatedBy()))
                .andExpect(jsonPath("$.secondSlot").value(sleepTrackerNotes.getSecondSlot()))
                .andExpect(
                        jsonPath("$.secondUpdatedBy").value(sleepTrackerNotes.getSecondUpdatedBy()))
                .andExpect(jsonPath("$.thirdSlot").value(sleepTrackerNotes.getThirdSlot()))
                .andExpect(
                        jsonPath("$.thirdUpdatedBy").value(sleepTrackerNotes.getThirdUpdatedBy()))
                .andExpect(jsonPath("$.fourthSlot").value(sleepTrackerNotes.getFourthSlot()))
                .andExpect(
                        jsonPath("$.fourthUpdatedBy").value(sleepTrackerNotes.getFourthUpdatedBy()))
                .andExpect(jsonPath("$.fifthSlot").value(sleepTrackerNotes.getFifthSlot()))
                .andExpect(
                        jsonPath("$.fifthUpdatedBy").value(sleepTrackerNotes.getFifthUpdatedBy()))
                .andExpect(jsonPath("$.sixthSlot").value(sleepTrackerNotes.getSixthSlot()))
                .andExpect(
                        jsonPath("$.sixthUpdatedBy").value(sleepTrackerNotes.getSixthUpdatedBy()))
                .andExpect(jsonPath("$.seventhSlot").value(sleepTrackerNotes.getSeventhSlot()))
                .andExpect(
                        jsonPath("$.seventhUpdatedBy")
                                .value(sleepTrackerNotes.getSeventhUpdatedBy()))
                .andExpect(jsonPath("$.eighthSlot").value(sleepTrackerNotes.getEighthSlot()))
                .andExpect(
                        jsonPath("$.eighthUpdatedBy").value(sleepTrackerNotes.getEighthUpdatedBy()))
                .andExpect(
                        jsonPath("$.reportDate")
                                .value(
                                        reportDate.format(
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))))
                .andExpect(jsonPath("$.id").value(sleepTrackerNotes.getId()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update SleepTrackerNote success")
    void testUpdateSleepTrackerNote_Success() {
        // Given
        String firstSlot = "Awake at 12:00am";
        String firstUpdatedBy = "mary";
        String secondSlot = "Awake at 1:00am";
        String secondUpdatedBy = "zen";
        String thirdSlot = "";
        String thirdUpdatedBy = "";
        String fourthSlot = "Awake at 2:00am";
        String fourthUpdatedBy = "mary";
        String fifthSlot = "Awake at 2:00am";
        String fifthUpdatedBy = "mary";
        String sixthSlot = "";
        String sixthUpdatedBy = "";
        String seventhSlot = "Awake at 2:00am";
        String seventhUpdatedBy = "mary";
        String eighthSlot = "Awake at 2:00am";
        String eighthUpdatedBy = "mary";
        Instant lastUpdatedAt = Instant.now();
        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);
        SleepTrackerNotes sleepTrackerNotes =
                SleepTrackerNotes.builder()
                        .client(client)
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Awake at 6:00am")
                        .seventhSlot("")
                        .seventhUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        sleepTrackerNotesRepository.save(sleepTrackerNotes);
        UpdateSleepTrackerRequest request =
                UpdateSleepTrackerRequest.builder()
                        .id(sleepTrackerNotes.getId())
                        .firstSlot(firstSlot)
                        .firstUpdatedBy(firstUpdatedBy)
                        .secondSlot(secondSlot)
                        .secondUpdatedBy(secondUpdatedBy)
                        .thirdSlot(thirdSlot)
                        .thirdUpdatedBy(thirdUpdatedBy)
                        .fourthSlot(fourthSlot)
                        .fourthUpdatedBy(fourthUpdatedBy)
                        .fifthSlot(fifthSlot)
                        .fifthUpdatedBy(fifthUpdatedBy)
                        .sixthSlot(sixthSlot)
                        .sixthUpdatedBy(sixthUpdatedBy)
                        .seventhSlot(seventhSlot)
                        .seventhUpdatedBy(seventhUpdatedBy)
                        .eighthSlot(eighthSlot)
                        .eighthUpdatedBy(eighthUpdatedBy)
                        .currentLastUpdatedAt(
                                sleepTrackerNotes.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();
        // When
        mockMvc.perform(
                        put("/sleep-notes/update/{id}", request.getId())
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
    @DisplayName("PUT /update SleepTrackerNote failure")
    void testUpdateSleepTrackerNote_Failure() {
        // Given
        String firstSlot = "Awake at 12:00am";
        String firstUpdatedBy = "mary";
        String secondSlot = "Awake at 1:00am";
        String secondUpdatedBy = "zen";
        String thirdSlot = "";
        String thirdUpdatedBy = "";
        String fourthSlot = "Awake at 2:00am";
        String fourthUpdatedBy = "mary";
        String fifthSlot = "Awake at 2:00am";
        String fifthUpdatedBy = "mary";
        String sixthSlot = "";
        String sixthUpdatedBy = "";
        String seventhSlot = "Awake at 2:00am";
        String seventhUpdatedBy = "mary";
        String eighthSlot = "Awake at 2:00am";
        String eighthUpdatedBy = "mary";
        Instant lastUpdatedAt = Instant.now();
        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);
        SleepTrackerNotes sleepTrackerNotes =
                SleepTrackerNotes.builder()
                        .client(client)
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        sleepTrackerNotesRepository.save(sleepTrackerNotes);
        UpdateSleepTrackerRequest request =
                UpdateSleepTrackerRequest.builder()
                        .id(sleepTrackerNotes.getId())
                        .firstSlot(firstSlot)
                        .firstUpdatedBy(firstUpdatedBy)
                        .secondSlot(secondSlot)
                        .secondUpdatedBy(secondUpdatedBy)
                        .thirdSlot(thirdSlot)
                        .thirdUpdatedBy(thirdUpdatedBy)
                        .fourthSlot(fourthSlot)
                        .fourthUpdatedBy(fourthUpdatedBy)
                        .fifthSlot(fifthSlot)
                        .fifthUpdatedBy(fifthUpdatedBy)
                        .sixthSlot(sixthSlot)
                        .sixthUpdatedBy(sixthUpdatedBy)
                        .seventhSlot(seventhSlot)
                        .seventhUpdatedBy(seventhUpdatedBy)
                        .eighthSlot(eighthSlot)
                        .eighthUpdatedBy(eighthUpdatedBy)
                        .currentLastUpdatedAt(
                                sleepTrackerNotes.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();
        // When
        mockMvc.perform(
                        put("/sleep-notes/update/{id}", 400)
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
}
