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
import com.onedoorway.project.dto.FoodDiaryNoteRequest;
import com.onedoorway.project.dto.ParticularFoodDiaryNoteRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FoodDiaryNoteRepository;
import com.onedoorway.project.repository.UserRepository;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FoodDiaryNoteControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private FoodDiaryNoteRepository foodDiaryNoteRepository;

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
        foodDiaryNoteRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create food diary for BREAKFAST")
    void createFoodReport_Success_BREAKFAST() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("BREAKFAST")
                        .mealTime("2021-10-13 08:00:00")
                        .mealFood("mealFood")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("POST /create food diary for MORNING SNACK")
    void createFoodReport_Success_MORNSNACK() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("MORN_SNACK")
                        .mealTime("2021-10-13 10:00:00")
                        .mealFood("mealFood")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("POST /create food diary for LUNCH")
    void createFoodReport_Success_LUNCH() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("LUNCH")
                        .mealTime("2021-10-13 12:30:00")
                        .mealFood("mealFood")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("POST /create food diary for AFTER SNACK")
    void createFoodReport_Success_AFTERSNACK() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("AFTER_SNACK")
                        .mealTime("2021-10-13 04:00:00")
                        .mealFood("mealFood")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("POST /create food diary for DINNER")
    void createFoodReport_Success_DINNER() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("DINNER")
                        .mealTime("2021-10-13 07:00:00")
                        .mealFood("mealFood")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("POST /create food diary for EVENING SNACK")
    void createFoodReport_Success_EVESNACK() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("EVE_SNACK")
                        .mealTime("2021-10-13 09:00:00")
                        .mealFood("mealFood")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("POST /create food diary without fields")
    void createFoodReport_Success_WithoutFields() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .mealType("BREAKFAST")
                        .mealTime("2021-10-13 08:00:00")
                        .mealComments("mealComments")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .mealUpdatedBy("mealUpdateBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/create")
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
    @DisplayName("GET / get food diary note")
    void testGetFoodDiaryNote_Success() {
        // Given
        long id = 1;
        LocalDate reportDate = LocalDate.now();
        Instant lastUpdatedAt = Instant.now();
        LocalDateTime mealTime = LocalDateTime.now();

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .client(client)
                        .mealType(MealType.BREAKFAST)
                        .mealTime(mealTime)
                        .mealFood("bread")
                        .mealDrink("milk")
                        .mealComments("good")
                        .mealUpdatedBy("joy")
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        foodDiaryNoteRepository.save(foodDiaryNote);

        // When
        mockMvc.perform(
                        get("/food-diary/get/{clientId}/{mealType}", client.getId(), "BREAKFAST")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").value(foodDiaryNote.getId()))
                .andExpect(jsonPath("$.mealType").value("BREAKFAST"))
                .andExpect(jsonPath("$.mealFood").value(foodDiaryNote.getMealFood()))
                .andExpect(jsonPath("$.mealDrink").value(foodDiaryNote.getMealDrink()))
                .andExpect(jsonPath("$.mealComments").value(foodDiaryNote.getMealComments()))
                .andExpect(jsonPath("$.mealUpdatedBy").value(foodDiaryNote.getMealUpdatedBy()))
                .andExpect(
                        jsonPath("$.mealTime")
                                .value(
                                        mealTime.format(
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss"))))
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
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update food diary success")
    void testUpdateFoodDiaryNote_Success() {
        // Given
        String mealType = "BREAKFAST";
        String mealFood = "bread";
        String mealDrink = "milk";
        String mealComments = "good";
        String mealTime = "2021-10-16 07:00:00";
        String mealUpdatedBy = "joe";
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .client(client)
                        .mealType(MealType.BREAKFAST)
                        .mealTime(LocalDateTime.now())
                        .mealFood("egg")
                        .mealDrink("juice")
                        .mealComments("healthy")
                        .mealUpdatedBy("juana")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        foodDiaryNote = foodDiaryNoteRepository.save(foodDiaryNote);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .id(foodDiaryNote.getId())
                        .mealFood(mealFood)
                        .mealType(mealType)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .mealTime(mealTime)
                        .mealUpdatedBy(mealUpdatedBy)
                        .currentLastUpdatedAt(
                                foodDiaryNote.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();

        // When
        mockMvc.perform(
                        put("/food-diary/update/{id}", request.getId())
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
    @DisplayName("PUT /update food diary failure")
    void testUpdateFoodDiaryNote_Failure() {
        // Given
        String mealType = "BREAKFAST";
        String mealFood = "bread";
        String mealDrink = "milk";
        String mealComments = "good";
        String mealTime = "2021-10-16 07:00:00";
        String mealUpdatedBy = "joe";
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .client(client)
                        .mealType(MealType.BREAKFAST)
                        .mealTime(LocalDateTime.now())
                        .mealFood("egg")
                        .mealDrink("juice")
                        .mealComments("healthy")
                        .mealUpdatedBy("juana")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        foodDiaryNote = foodDiaryNoteRepository.save(foodDiaryNote);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .id(foodDiaryNote.getId())
                        .mealFood(mealFood)
                        .mealType(mealType)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .mealTime(mealTime)
                        .mealUpdatedBy(mealUpdatedBy)
                        .currentLastUpdatedAt(
                                foodDiaryNote.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();

        // When
        mockMvc.perform(
                        put("/food-diary/update/{id}", 400)
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
    @DisplayName("GET / get particular food diary note")
    void testGetParticularFoodDiaryNote_Success() {
        // Given
        long id = 1;
        String date = "2021-09-09";
        String meal = "BREAKFAST";
        LocalDate reportDate = LocalDate.parse(date);
        Instant lastUpdatedAt = Instant.now();
        LocalDateTime mealTime = LocalDateTime.now();

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .client(client)
                        .mealType(MealType.BREAKFAST)
                        .mealTime(mealTime)
                        .mealFood("bread")
                        .mealDrink("milk")
                        .mealComments("good")
                        .mealUpdatedBy("joy")
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        foodDiaryNoteRepository.save(foodDiaryNote);

        ParticularFoodDiaryNoteRequest request =
                ParticularFoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .reportDate(date)
                        .mealType(meal)
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/get-particular")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").value(foodDiaryNote.getId()))
                .andExpect(jsonPath("$.mealType").value("BREAKFAST"))
                .andExpect(jsonPath("$.mealFood").value(foodDiaryNote.getMealFood()))
                .andExpect(jsonPath("$.mealDrink").value(foodDiaryNote.getMealDrink()))
                .andExpect(jsonPath("$.mealComments").value(foodDiaryNote.getMealComments()))
                .andExpect(jsonPath("$.mealUpdatedBy").value(foodDiaryNote.getMealUpdatedBy()))
                .andExpect(
                        jsonPath("$.mealTime")
                                .value(
                                        mealTime.format(
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss"))))
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
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get particular food diary note failure")
    void testGetParticularFoodDiaryNote_Failure() {
        // Given
        long id = 1;
        String date = "2021-09-09";
        String meal = "BREAKFAST";
        ParticularFoodDiaryNoteRequest request =
                ParticularFoodDiaryNoteRequest.builder()
                        .clientId(id)
                        .reportDate(date)
                        .mealType(meal)
                        .build();

        // When
        mockMvc.perform(
                        post("/food-diary/get-particular")
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
