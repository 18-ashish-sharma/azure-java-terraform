package com.onedoorway.project.controller;

import static com.onedoorway.project.model.LookupType.MISCELLANEOUS_NOTES;
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
import com.onedoorway.project.dto.ListMiscellaneousNoteRequest;
import com.onedoorway.project.dto.MiscellaneousNoteRequest;
import com.onedoorway.project.dto.UpdateMiscellaneousNoteRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.LookupRepository;
import com.onedoorway.project.repository.MiscellaneousNoteRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDate;
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
class MiscellaneousNoteControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private MiscellaneousNoteRepository miscellaneousNoteRepository;

    @Autowired private LookupRepository lookupRepository;

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
        miscellaneousNoteRepository.deleteAll();
        lookupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create miscellaneous note")
    void testCreateMiscellaneousNoteSuccess() {
        // Given
        Lookup category = Lookup.builder().id(1).name("category").build();
        category = lookupRepository.save(category);

        MiscellaneousNoteRequest request =
                MiscellaneousNoteRequest.builder()
                        .categoryId(category.getId())
                        .noteDate("2021-12-03")
                        .content("content")
                        .subject("subject")
                        .client("client")
                        .house("house")
                        .user("user")
                        .lastUploadedBy("sam")
                        .build();
        // When
        mockMvc.perform(
                        post("/miscellaneous-note/create")
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
    @DisplayName("POST /create miscellaneous note without fields")
    void testCreateMiscellaneousNoteSuccess_WithoutFields() {
        // Given
        Lookup category = Lookup.builder().id(1L).name("category").build();
        category = lookupRepository.save(category);

        MiscellaneousNoteRequest request =
                MiscellaneousNoteRequest.builder()
                        .categoryId(category.getId())
                        .noteDate("2021-12-03")
                        .content("content")
                        .subject("subject")
                        .lastUploadedBy("sam")
                        .build();
        // When
        mockMvc.perform(
                        post("/miscellaneous-note/create")
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
    @DisplayName("POST /create miscellaneous note failure")
    void testCreateMiscellaneousNoteFailure() {
        // Given
        MiscellaneousNoteRequest request =
                MiscellaneousNoteRequest.builder()
                        .categoryId(10)
                        .noteDate("2021-12-03")
                        .content(
                                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor.")
                        .subject("subject")
                        .house("house")
                        .user("user")
                        .client("client")
                        .lastUploadedBy("sam")
                        .build();

        // When
        mockMvc.perform(
                        post("/miscellaneous-note/create")
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
    @DisplayName("PUT /update miscellaneous note success")
    void testUpdateMiscellaneousNote_Success() {
        // Given
        String subject = "subject 11";
        String content = "Content 11";
        String house = "house";
        String client = "client";
        String user = "Tom";
        String lastUploadedBy = "sina";

        MiscellaneousNote miscellaneousNote =
                MiscellaneousNote.builder()
                        .subject(subject)
                        .content(content)
                        .house(house)
                        .client(client)
                        .user(user)
                        .deleted(false)
                        .lastUploadedBy("seema")
                        .lastUpdatedAt(Instant.now())
                        .build();
        miscellaneousNote = miscellaneousNoteRepository.save(miscellaneousNote);

        UpdateMiscellaneousNoteRequest request =
                UpdateMiscellaneousNoteRequest.builder()
                        .subject(subject)
                        .content(content)
                        .deleted(true)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/miscellaneous-note/update/{id}", miscellaneousNote.getId())
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
    void testUpdateMiscellaneousNote_Failure() {
        // Given
        String subject = "subject 11";
        String content = "Content 11";
        String house = "4";
        String client = "client";
        String user = "Tom";
        String lastUploadedBy = "sina";

        UpdateMiscellaneousNoteRequest request =
                UpdateMiscellaneousNoteRequest.builder()
                        .subject(subject)
                        .content(content)
                        .house(house)
                        .client(client)
                        .user(user)
                        .deleted(true)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/miscellaneous-note/update/{id}", 300)
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
    @DisplayName("GET / get all miscellaneous notes")
    void testListMiscellaneousNote() {
        // Given
        Lookup lookup = Lookup.builder().name("test").lookupType(MISCELLANEOUS_NOTES).build();
        lookup = lookupRepository.save(lookup);
        String client = "maya";
        String user = "unni";
        String house = "hi";

        MiscellaneousNote miscellaneousNote =
                MiscellaneousNote.builder()
                        .id(1)
                        .client(client)
                        .category(lookup)
                        .subject("subject")
                        .content("content")
                        .deleted(false)
                        .noteDate(LocalDate.parse("2021-11-18"))
                        .user(user)
                        .house(house)
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(Instant.now())
                        .build();
        miscellaneousNote = miscellaneousNoteRepository.save(miscellaneousNote);
        ListMiscellaneousNoteRequest request =
                ListMiscellaneousNoteRequest.builder()
                        .category(lookup.getName())
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        mockMvc.perform(
                        post("/miscellaneous-note/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(miscellaneousNote.getId()))
                .andExpect(jsonPath("$.[0].user").value(miscellaneousNote.getUser()))
                .andExpect(jsonPath("$.[0].house").value(miscellaneousNote.getHouse()))
                .andExpect(jsonPath("$.[0].client").value(miscellaneousNote.getClient()))
                .andExpect(jsonPath("$.[0].deleted").value(miscellaneousNote.getDeleted()))
                .andExpect(jsonPath("$.[0].subject").value(miscellaneousNote.getSubject()))
                .andExpect(jsonPath("$.[0].content").value(miscellaneousNote.getContent()))
                .andExpect(
                        jsonPath("$.[0].noteDate")
                                .value(
                                        miscellaneousNote
                                                .getNoteDate()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.[0].lastUploadedBy")
                                .value(miscellaneousNote.getLastUploadedBy()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        miscellaneousNote
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
