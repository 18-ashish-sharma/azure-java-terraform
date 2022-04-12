package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.BowelNoteRequest;
import com.onedoorway.project.dto.ListBowelNoteRequest;
import com.onedoorway.project.dto.UpdateBowelNoteRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.BowelNoteRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
class BowelNoteControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private BowelNoteRepository bowelNoteRepository;

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
        bowelNoteRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create bowel note")
    void testCreateBowelNoteSuccess() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);

        BowelNoteRequest request =
                BowelNoteRequest.builder()
                        .clientId(client.getId())
                        .size("Small")
                        .recordTime("2021-09-09 20:00:00")
                        .type1(true)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .startDate("2021-09-09")
                        .lastUploadedBy("sam")
                        .build();
        // When
        mockMvc.perform(
                        post("/bowel-note/create")
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
    @DisplayName("POST /create bowel note failure")
    void testCreateBowelNoteFailure() {
        // Given
        BowelNoteRequest request =
                BowelNoteRequest.builder()
                        .clientId(10)
                        .size("Small")
                        .recordTime("2021-09-09 20:00:00")
                        .type1(false)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .startDate("2021-09-09")
                        .lastUploadedBy("sam")
                        .build();

        // When
        mockMvc.perform(
                        post("/bowel-note/create")
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
    @DisplayName("POST /create bowel notes failure without fields")
    void createBowelNote_Failure_WithoutFields() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        BowelNoteRequest request =
                BowelNoteRequest.builder()
                        .size("Small")
                        .recordTime("2021-09-09 20:00:00")
                        .type1(true)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .startDate("2021-09-09")
                        .lastUploadedBy("sam")
                        .build();

        // When
        mockMvc.perform(
                        post("/bowel-note/create")
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
    @DisplayName("PUT /update bowel note success")
    void testUpdateBowelNote_Success() {
        // Given
        String recordTime = "2022-11-25 16:00:00";
        String size = "Small";
        String lastUploadedBy = "sam";
        String startDate = "2022-11-25";
        Long id = 1L;
        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        BowelNote bowelNote =
                BowelNote.builder()
                        .id(id)
                        .client(client)
                        .size(Size.Large)
                        .recordTime(LocalDateTime.now())
                        .type1(false)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .deleted(false)
                        .lastUploadedBy(lastUploadedBy)
                        .startDate(LocalDate.parse(startDate))
                        .lastUpdatedAt(Instant.now())
                        .build();

        bowelNote = bowelNoteRepository.save(bowelNote);

        UpdateBowelNoteRequest request =
                UpdateBowelNoteRequest.builder()
                        .size(size)
                        .type1(false)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .lastUploadedBy(lastUploadedBy)
                        .currentLastUpdatedAt(Instant.now())
                        .deleted(false)
                        .recordTime(recordTime)
                        .build();
        // when
        mockMvc.perform(
                        put("/bowel-note/update/{id}", bowelNote.getId())
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
    @DisplayName("PUT /update bowel note failure")
    void testUpdateBowelNote_Failure() {
        // Given

        UpdateBowelNoteRequest request =
                UpdateBowelNoteRequest.builder()
                        .size("Small")
                        .recordTime("2021-09-09 20:00:00")
                        .type1(false)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .deleted(false)
                        .lastUploadedBy("sam")
                        .build();

        // When
        mockMvc.perform(
                        put("/bowel-note/update/{id}", 300)
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

    @SneakyThrows
    @Test
    @DisplayName("GET / get all bowel notes")
    void testListBowelNotes() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        BowelNote bowelNote =
                BowelNote.builder()
                        .id(1)
                        .client(client)
                        .deleted(false)
                        .size(Size.Small)
                        .type1(false)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .startDate(LocalDate.parse("2021-12-09"))
                        .recordTime(LocalDateTime.now())
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(Instant.now())
                        .build();
        bowelNote = bowelNoteRepository.save(bowelNote);
        ListBowelNoteRequest request =
                ListBowelNoteRequest.builder()
                        .clientId(client.getId())
                        .start("2021-12-09")
                        .end("2021-12-12")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        mockMvc.perform(
                        post("/bowel-note/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(bowelNote.getId()))
                .andExpect(jsonPath("$.[0].clientId").value(bowelNote.getClient().getId()))
                .andExpect(jsonPath("[0].deleted").value(bowelNote.getDeleted()))
                .andExpect(jsonPath("[0].size").value(bowelNote.getSize().name()))
                .andExpect(jsonPath("[0].type1").value(bowelNote.getType1()))
                .andExpect(jsonPath("[0].type2").value(bowelNote.getType2()))
                .andExpect(jsonPath("[0].type3").value(bowelNote.getType3()))
                .andExpect(jsonPath("[0].type4").value(bowelNote.getType4()))
                .andExpect(jsonPath("[0].type5").value(bowelNote.getType5()))
                .andExpect(jsonPath("[0].type6").value(bowelNote.getType6()))
                .andExpect(jsonPath("[0].type7").value(bowelNote.getType7()))
                .andExpect(
                        jsonPath("[0].startDate")
                                .value(
                                        bowelNote
                                                .getStartDate()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("[0].recordTime")
                                .value(
                                        bowelNote
                                                .getRecordTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.[0].lastUploadedBy").value(bowelNote.getLastUploadedBy()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        bowelNote
                                                .getLastUpdatedAt()
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
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
