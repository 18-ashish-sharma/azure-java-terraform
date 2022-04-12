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
import com.onedoorway.project.dto.ListNoticeRequest;
import com.onedoorway.project.dto.NoticeRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.NoticeRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
class NoticeControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private NoticeRepository noticeRepository;

    @Autowired private HouseRepository houseRepository;

    private Notice testNotice;

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
        noticeRepository.deleteAll();
        userRepository.deleteAll();
        houseRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create notice")
    void testCreateNoticesSuccess() {
        // Given
        String notice = "Test notice";
        String houseCode = "101";
        Instant createdAt = Instant.now();
        House house = House.builder().houseCode(houseCode).id(1).build();
        houseRepository.save(house);
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice(notice)
                        .houseCode(List.of(house.getHouseCode()))
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();
        // When
        mockMvc.perform(
                        post("/notice/create")
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
    @DisplayName("POST /create notice when fields are blank")
    void testCreateNotice_WithoutFields() {
        // Given
        String notice = "Test notice";
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).id(1).build();
        houseRepository.save(house);
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice(notice)
                        .houseCode(List.of(house.getHouseCode()))
                        .build();
        // When
        mockMvc.perform(
                        post("/notice/create")
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
    @DisplayName("GET / get notice")
    void testGetNotice_Success() {
        // Given
        House house = House.builder().houseCode("101").build();
        house = houseRepository.save(house);

        User user = User.builder().id(1).email("tom@test.com").password("password").build();
        user = userRepository.save(user);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Instant createdAt = Instant.now();
        LocalDateTime localCreatedAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);

        String notice = "checkup";
        testNotice =
                Notice.builder()
                        .id(1)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .houses(Set.of(house))
                        .createdBy(user)
                        .notice(notice)
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .createdAt(createdAt)
                        .build();
        testNotice = noticeRepository.save(testNotice);

        // When
        mockMvc.perform(
                        get("/notice/get/{id}", testNotice.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testNotice.getId()))
                .andExpect(jsonPath("$.noticeStatus").value(testNotice.getNoticeStatus().name()))
                .andExpect(jsonPath("$.notice").value(testNotice.getNotice()))
                .andExpect(jsonPath("$.createdAt").value(localCreatedAt.format(formatter)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST / list notice")
    void testListNotices_Success() {
        // Given
        House house = House.builder().houseCode("151").build();
        house = houseRepository.save(house);

        User user = User.builder().id(1).email("tom@test.com").password("password").build();
        user = userRepository.save(user);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Instant createdAt = Instant.now();
        LocalDateTime localCreatedAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);

        String notice = "checkup";
        testNotice =
                Notice.builder()
                        .id(2)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .houses(Set.of(house))
                        .createdBy(user)
                        .notice(notice)
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .createdAt(createdAt)
                        .build();
        testNotice = noticeRepository.save(testNotice);
        ListNoticeRequest request =
                ListNoticeRequest.builder()
                        .status("ACTIVE")
                        .houseCode("151")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        mockMvc.perform(
                        post("/notice/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testNotice.getId()))
                .andExpect(jsonPath("$[0].noticeStatus").value(testNotice.getNoticeStatus().name()))
                .andExpect(jsonPath("$[0].notice").value(testNotice.getNotice()))
                .andExpect(jsonPath("$[0].createdAt").value(localCreatedAt.format(formatter)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update notice")
    void testUpdateNoticeSuccess() {
        // Given
        String notice = "Test notice";
        String houseCode = "191";
        Instant createdAt = Instant.now();
        House house = House.builder().houseCode(houseCode).build();
        houseRepository.save(house);
        User user = User.builder().id(1).email("tom@test.com").password("password").build();
        user = userRepository.save(user);
        testNotice =
                Notice.builder()
                        .id(1)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .houses(Set.of(house))
                        .createdBy(user)
                        .notice(notice)
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .createdAt(createdAt)
                        .build();
        testNotice = noticeRepository.save(testNotice);
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice("Updated notice")
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .houseCode(List.of(house.getHouseCode()))
                        .build();

        // When
        mockMvc.perform(
                        put("/notice/update/" + testNotice.getId())
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
    @DisplayName("PUT / update notice")
    void testUpdateNotice_Failure_Notice_NotPresent() {
        House house = House.builder().houseCode("101").build();
        house = houseRepository.save(house);
        User user = User.builder().id(1).email("tom@test.com").password("password").build();
        user = userRepository.save(user);
        Instant createdAt = Instant.now();
        testNotice =
                Notice.builder()
                        .id(1)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .houses(Set.of(house))
                        .createdBy(user)
                        .notice("notice 1")
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .createdAt(createdAt)
                        .build();
        testNotice = noticeRepository.save(testNotice);
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice("notice 2")
                        .houseCode(List.of(house.getHouseCode()))
                        .noticeStatus(NoticeStatus.INACTIVE.name())
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .build();
        // When
        mockMvc.perform(
                        put("/notice/update/{id}", 1000)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isBadRequest());
    }
}
