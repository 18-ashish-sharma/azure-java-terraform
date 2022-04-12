package com.onedoorway.project.controller;

import static com.onedoorway.project.model.LookupType.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.CaseNoteRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.LookupRepository;
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
class CaseNoteControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private CaseNoteRepository caseNoteRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private LookupRepository lookupRepository;

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
        caseNoteRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
        lookupRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create case note")
    void testCreateCaseNoteSuccess() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);
        Lookup category = Lookup.builder().id(1).name("category").build();
        category = lookupRepository.save(category);

        CaseNoteRequest request =
                CaseNoteRequest.builder()
                        .clientId(client.getId())
                        .categoryId(category.getId())
                        .content("content")
                        .subject("subject")
                        .startTime("2021-09-09 20:00:00")
                        .endTime("2021-09-09 08:00:00")
                        .noteDate("2021-09-09")
                        .lastUploadedBy("sam")
                        .build();
        // When
        mockMvc.perform(
                        post("/case-note/create")
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
    @DisplayName("POST /create case note without fields")
    void testCreateCaseNoteSuccess_WithoutFields() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);
        Lookup category = Lookup.builder().id(1).name("category").build();
        category = lookupRepository.save(category);

        CaseNoteRequest request =
                CaseNoteRequest.builder()
                        .clientId(client.getId())
                        .categoryId(category.getId())
                        .startTime("2021-09-09 20:00:00")
                        .endTime("2021-09-09 08:00:00")
                        .noteDate("2021-09-09")
                        .build();
        // When
        mockMvc.perform(
                        post("/case-note/create")
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
    @DisplayName("POST /create case note failure")
    void testCreateCaseNoteFailure() {
        // Given
        CaseNoteRequest request =
                CaseNoteRequest.builder()
                        .categoryId(12)
                        .clientId(10)
                        .content("content")
                        .subject("subject")
                        .startTime("2021-09-09 20:00:00")
                        .endTime("2021-09-09 08:00:00")
                        .noteDate("2021-09-09")
                        .lastUploadedBy("sam")
                        .build();

        // When
        mockMvc.perform(
                        post("/case-note/create")
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
    @DisplayName("GET / get case note")
    void testGetCaseNote() {
        // Given
        long id = 1;
        String date = "2021-09-09";
        LocalDate noteDate = LocalDate.parse(date);
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        lookup = lookupRepository.save(lookup);

        CaseNote caseNote =
                CaseNote.builder()
                        .client(client)
                        .category(lookup)
                        .noteDate(noteDate)
                        .subject("sub 1")
                        .content("content 1")
                        .deleted(false)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        caseNoteRepository.save(caseNote);

        // When
        mockMvc.perform(
                        get("/case-note/get/{id}", caseNote.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(caseNote.getClient().getId()))
                .andExpect(
                        jsonPath("$.category")
                                .value(
                                        LookupDTO.builder()
                                                .id(lookup.getId())
                                                .name(lookup.getName())
                                                .build()))
                .andExpect(jsonPath("$.content").value(caseNote.getContent()))
                .andExpect(jsonPath("$.subject").value(caseNote.getSubject()))
                .andExpect(jsonPath("$.deleted").value(caseNote.getDeleted()))
                .andExpect(
                        jsonPath("$.noteDate")
                                .value(
                                        caseNote.getNoteDate()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.startTime")
                                .value(
                                        caseNote.getStartTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(
                        jsonPath("$.endTime")
                                .value(
                                        caseNote.getEndTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.lastUploadedBy").value(caseNote.getLastUploadedBy()))
                .andExpect(
                        jsonPath("$.lastUpdatedAt")
                                .value(
                                        caseNote.getLastUpdatedAt()
                                                .atOffset((ZoneOffset.UTC))
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get case note failure")
    void testGetCaseNote_Failure() {
        // When
        mockMvc.perform(
                        get("/case-note/get/{id}", 10)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get all case notes")
    void testListCaseNotes() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        lookup = lookupRepository.save(lookup);

        CaseNote caseNote =
                CaseNote.builder()
                        .id(1)
                        .client(client)
                        .category(lookup)
                        .subject("subject")
                        .content("content")
                        .deleted(false)
                        .noteDate(LocalDate.parse("2021-11-18"))
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(Instant.now())
                        .build();
        caseNote = caseNoteRepository.save(caseNote);

        ListCaseNoteRequest request =
                ListCaseNoteRequest.builder()
                        .clientId(client.getId())
                        .category(lookup.getName())
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        mockMvc.perform(
                        post("/case-note/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(caseNote.getId()))
                .andExpect(jsonPath("$.[0].clientId").value(caseNote.getClient().getId()))
                .andExpect(
                        jsonPath("$.[0].category")
                                .value(
                                        LookupDTO.builder()
                                                .id(lookup.getId())
                                                .name(lookup.getName())
                                                .build()))
                .andExpect(jsonPath("$.[0].subject").value(caseNote.getSubject()))
                .andExpect(jsonPath("$.[0].content").value(caseNote.getContent()))
                .andExpect(jsonPath("$.[0].deleted").value(caseNote.getDeleted()))
                .andExpect(
                        jsonPath("$.[0].noteDate")
                                .value(
                                        caseNote.getNoteDate()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.[0].startTime")
                                .value(
                                        caseNote.getStartTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(
                        jsonPath("$.[0].endTime")
                                .value(
                                        caseNote.getEndTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.[0].lastUploadedBy").value(caseNote.getLastUploadedBy()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        caseNote.getLastUpdatedAt()
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update case note success")
    void testUpdateCaseNote_Success() {
        // Given
        String subject = "subject 11";
        String content = "Content 11";
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        String noteDate = "2021-12-11";
        String lastUploadedBy = "sina";
        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);
        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        lookup = lookupRepository.save(lookup);

        CaseNote caseNote =
                CaseNote.builder()
                        .client(client)
                        .category(lookup)
                        .content("content")
                        .subject("subject")
                        .deleted(false)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .lastUploadedBy("seema")
                        .noteDate(LocalDate.now())
                        .lastUpdatedAt(Instant.now())
                        .build();
        caseNote = caseNoteRepository.save(caseNote);

        UpdateCaseNoteRequest request =
                UpdateCaseNoteRequest.builder()
                        .subject(subject)
                        .categoryId(lookup.getId())
                        .content(content)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .deleted(true)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/case-note/update/{id}", caseNote.getId())
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
    @DisplayName("PUT /update case note failure")
    void testUpdateCaseNote_Failure() {
        // Given
        String subject = "subject 11";
        String content = "Content 11";
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        String lastUploadedBy = "sina";
        String noteDate = "2021-11-12";

        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        lookup = lookupRepository.save(lookup);

        UpdateCaseNoteRequest request =
                UpdateCaseNoteRequest.builder()
                        .subject(subject)
                        .categoryId(lookup.getId())
                        .content(content)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .deleted(true)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/case-note/update/{id}", 300)
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
