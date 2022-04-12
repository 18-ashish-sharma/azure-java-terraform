package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.DailyNoteRequest;
import com.onedoorway.project.dto.SearchDailyNoteRequest;
import com.onedoorway.project.dto.UpdateDailyNoteRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DailyNoteControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private RoleRepository roleRepository;

    @Autowired private HouseRepository houseRepository;

    @Autowired private DailyNoteRepository dailyNoteRepository;

    @Autowired private ClientRepository clientRepository;

    private Client testClient;

    @BeforeAll
    public void setup() {
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

        House house = House.builder().houseCode("501").build();
        houseRepository.save(house);

        String dob = "1957-01-01";
        testClient =
                Client.builder()
                        .name("name")
                        .gender("gender")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("clientfordn@test.com")
                        .phone("phone")
                        .addrLine1("address1")
                        .addrLine2("address2")
                        .city("city")
                        .state("state")
                        .postCode("1234")
                        .build();

        testClient = clientRepository.save(testClient);

        ODWUserDetails basicUser = new ODWUserDetails(user);
        when(mockJwtUtil.extractUsername(anyString())).thenReturn("test@test.com");
        when(mockJwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(mockUserDetailsService.loadUserByUsername(anyString())).thenReturn(basicUser);

        this.mockMvc =
                webAppContextSetup(this.wac)
                        .addFilters(new JwtRequestFilter(mockUserDetailsService, mockJwtUtil))
                        .build();
    }

    @AfterAll
    public void teardown() {
        dailyNoteRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create note")
    void testCreateNotesSuccess() {
        // Given
        String note = "Checked everything";
        String houseCode = "501";
        DailyNoteRequest dailyNoteRequest =
                DailyNoteRequest.builder()
                        .note(note)
                        .houseCode(houseCode)
                        .startTime("2021-09-09 20:00:00")
                        .endTime("2021-09-09 08:00:00")
                        .clientId(testClient.getId())
                        .build();
        // When
        mockMvc.perform(
                        post("/dailynote/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(dailyNoteRequest)))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET /notes")
    void testGetHouseCodeAndCreatedBy_IdSuccess() {
        // Given
        String houseCode = "502";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Instant createdAt = Instant.now();
        LocalDateTime localCreatedAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);

        User user =
                User.builder()
                        .email("gettest@test.com")
                        .password("test")
                        .firstName("first")
                        .lastName("last")
                        .build();
        user = userRepository.save(user);
        long userId = user.getId();

        DailyNote dailyNote =
                DailyNote.builder()
                        .house(house)
                        .client(testClient)
                        .createBy(user)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .note("Cleaned office area")
                        .createdAt(createdAt)
                        .build();
        dailyNote = dailyNoteRepository.save(dailyNote);

        // When
        mockMvc.perform(
                        get("/dailynote/get/{userId}/{houseCode}", userId, houseCode)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(dailyNote.getId()))
                .andExpect(
                        jsonPath("$.[0].startTime")
                                .value(
                                        dailyNote
                                                .getStartTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(
                        jsonPath("$.[0].endTime")
                                .value(
                                        dailyNote
                                                .getEndTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.[0].createById").value(dailyNote.getCreateBy().getId()))
                .andExpect(
                        jsonPath("$.[0].createByEmail").value(dailyNote.getCreateBy().getEmail()))
                .andExpect(
                        jsonPath("$.[0].createByFirstName")
                                .value(dailyNote.getCreateBy().getFirstName()))
                .andExpect(
                        jsonPath("$.[0].createByLastName")
                                .value(dailyNote.getCreateBy().getLastName()))
                .andExpect(jsonPath("$.[0].note").value(dailyNote.getNote()))
                .andExpect(jsonPath("$.[0].createdAt").value(localCreatedAt.format(formatter)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /search")
    void testSearch() {
        // Given
        String houseCode = "503";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Instant createdAt = Instant.now();
        LocalDateTime localCreatedAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);

        User user =
                User.builder()
                        .email("searchtest@test.com")
                        .password("test")
                        .firstName("first")
                        .lastName("last")
                        .build();
        user = userRepository.save(user);

        DailyNote dailyNote =
                DailyNote.builder()
                        .house(house)
                        .createBy(user)
                        .note("Cleaned office area")
                        .client(testClient)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .createBy(user)
                        .createdAt(createdAt)
                        .build();
        dailyNote = dailyNoteRepository.save(dailyNote);
        SearchDailyNoteRequest request =
                SearchDailyNoteRequest.builder()
                        .houseCode(houseCode)
                        .clientId(testClient.getId())
                        .start(createdAt.minus(5, ChronoUnit.MINUTES))
                        .end(createdAt.plus(5, ChronoUnit.MINUTES))
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        mockMvc.perform(
                        post("/dailynote/search")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(dailyNote.getId()))
                .andExpect(jsonPath("$.[0].note").value(dailyNote.getNote()))
                .andExpect(jsonPath("$.[0].createById").value(dailyNote.getCreateBy().getId()))
                .andExpect(
                        jsonPath("$.[0].createByEmail").value(dailyNote.getCreateBy().getEmail()))
                .andExpect(
                        jsonPath("$.[0].createByFirstName")
                                .value(dailyNote.getCreateBy().getFirstName()))
                .andExpect(
                        jsonPath("$.[0].createByLastName")
                                .value(dailyNote.getCreateBy().getLastName()))
                .andExpect(
                        jsonPath("$.[0].startTime")
                                .value(
                                        dailyNote
                                                .getStartTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(
                        jsonPath("$.[0].endTime")
                                .value(
                                        dailyNote
                                                .getEndTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.[0].createdAt").value(localCreatedAt.format(formatter)));
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /delete note")
    void testDeleteNotes() {
        // Given
        String houseCode = "504";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);

        Instant createdAt = Instant.now();
        User user = User.builder().email("deletetest@test.com").password("test").build();
        user = userRepository.save(user);

        DailyNote dailyNote =
                DailyNote.builder()
                        .house(house)
                        .client(testClient)
                        .createBy(user)
                        .note("Cleaned office area")
                        .createdAt(createdAt)
                        .build();
        final DailyNote createdNote = dailyNoteRepository.save(dailyNote);
        long id = createdNote.getId();
        // When
        mockMvc.perform(
                        delete("/dailynote/delete/{id}", id)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));

        // assert that the deletion is success
        assertEquals(dailyNoteRepository.findAll().size(), 0);
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /delete note that does not exist")
    void testDeleteNotes_NotExist() {
        // Given
        long id = 100;
        // When
        mockMvc.perform(
                        delete("/dailynote/delete/{id}", id)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update note")
    void testUpdateNotes() {
        // Given
        String note = "updated";
        String houseCode = "101";
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);
        Instant createdAt = Instant.now();
        User user =
                User.builder()
                        .email("testupdlynts@test.com")
                        .firstName("test")
                        .lastName("one")
                        .password("test")
                        .build();
        user = userRepository.save(user);

        DailyNote dailyNote =
                DailyNote.builder()
                        .house(house)
                        .client(testClient)
                        .createBy(user)
                        .note("welcome")
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .createdAt(createdAt)
                        .build();
        dailyNote = dailyNoteRepository.save(dailyNote);

        UpdateDailyNoteRequest request =
                UpdateDailyNoteRequest.builder()
                        .note(note)
                        .id(dailyNote.getId())
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        // When
        mockMvc.perform(
                        put("/dailynote/update")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update note failure")
    void testUpdateNotes_Failure() {
        // Given
        String note = "updated";
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        UpdateDailyNoteRequest request =
                UpdateDailyNoteRequest.builder()
                        .note(note)
                        .id(1L)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        // When
        mockMvc.perform(
                        put("/dailynote/update")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

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
