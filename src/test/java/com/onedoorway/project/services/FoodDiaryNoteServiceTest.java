package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.FoodDiaryNoteDTO;
import com.onedoorway.project.dto.FoodDiaryNoteRequest;
import com.onedoorway.project.dto.ParticularFoodDiaryNoteRequest;
import com.onedoorway.project.exception.FoodDiaryNoteServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.FoodDiaryNote;
import com.onedoorway.project.model.MealType;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FoodDiaryNoteRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class FoodDiaryNoteServiceTest {
    @Mock FoodDiaryNoteRepository mockFoodDiaryNoteRepository;

    @Mock ClientRepository mockClientRepository;

    private FoodDiaryNoteService foodDiaryNoteService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        foodDiaryNoteService =
                new FoodDiaryNoteService(
                        mockFoodDiaryNoteRepository, mockClientRepository, context);
    }

    @SneakyThrows
    @Test
    void testCreateFoodReport_Success_BREAKFAST() {
        // Given
        Long id = 1L;
        String mealType = "BREAKFAST";
        String mealTime = "2021-10-13 08:00:00";
        String mealFood = "bread";
        String mealDrink = "juice";
        String mealComments = "good";
        String mealUpdateBy = "tom";
        LocalDate reportDate = LocalDate.of(2021, 10, 14);
        Instant lastReportedAt = context.now();

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(1L)
                        .mealType(mealType)
                        .mealTime(mealTime)
                        .mealFood(mealFood)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .reportDate(reportDate)
                        .mealUpdatedBy(mealUpdateBy)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        foodDiaryNoteService.createFoodReport(request);

        // Then
        FoodDiaryNote expected =
                FoodDiaryNote.builder()
                        .id(id)
                        .client(client)
                        .mealType(MealType.BREAKFAST)
                        .mealTime(
                                LocalDateTime.parse(
                                        mealTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .mealFood(mealFood)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .mealUpdatedBy(mealUpdateBy)
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastReportedAt)
                        .build();

        ArgumentCaptor<FoodDiaryNote> foodReportArgumentCaptor =
                ArgumentCaptor.forClass(FoodDiaryNote.class);
        verify(mockFoodDiaryNoteRepository).save(foodReportArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("mealType", equalTo(MealType.BREAKFAST)),
                        HasPropertyWithValue.hasProperty("mealFood", equalTo("bread")),
                        HasPropertyWithValue.hasProperty("mealDrink", equalTo("juice")),
                        HasPropertyWithValue.hasProperty("mealComments", equalTo("good")),
                        HasPropertyWithValue.hasProperty("mealUpdatedBy", equalTo("tom")),
                        HasPropertyWithValue.hasProperty(
                                "mealTime",
                                equalTo(
                                        LocalDateTime.parse(
                                                mealTime,
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss")))),
                        HasPropertyWithValue.hasProperty(
                                "reportDate", equalTo(LocalDate.of(2021, 10, 14))))));
    }

    @Test
    void testCreateFoodReport_Failure_NoClientFound() {
        // Given
        String mealType = "BREAKFAST";
        String mealTime = "2021-10-13 08:00:00";
        String mealFood = "bread";
        String mealDrink = "juice";
        String mealComments = "good";
        String mealUpdateBy = "tom";
        LocalDate reportDate = LocalDate.of(2021, 10, 14);

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .clientId(1L)
                        .mealType(mealType)
                        .mealTime(mealTime)
                        .mealFood(mealFood)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .reportDate(reportDate)
                        .mealUpdatedBy(mealUpdateBy)
                        .build();

        assertThrows(
                FoodDiaryNoteServiceException.class,
                () -> {

                    // When
                    foodDiaryNoteService.createFoodReport(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetFoodDiaryNote_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .id(1L)
                        .client(client)
                        .mealType(MealType.EVE_SNACK)
                        .mealTime(LocalDateTime.now())
                        .mealFood("sandwich")
                        .mealDrink("juice")
                        .mealComments("healthy")
                        .mealUpdatedBy("issa")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockFoodDiaryNoteRepository.findByClient_IdAndMealTypeAndReportDate(
                        1L, foodDiaryNote.getMealType(), LocalDate.now()))
                .thenReturn(foodDiaryNote);
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        FoodDiaryNoteDTO expected = new ModelMapper().map(foodDiaryNote, FoodDiaryNoteDTO.class);

        // When
        FoodDiaryNoteDTO actual = foodDiaryNoteService.getFoodDiaryNote(1L, "EVE_SNACK");

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateFoodDiaryNote_Success() {
        // Given
        long id = 1L;
        String mealType = "BREAKFAST";
        String mealFood = "bread";
        String mealDrink = "milk";
        String mealComments = "good";
        String mealTime = "2021-10-16 07:00:00";
        String mealUpdatedBy = "joe";
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(1).name("client").build();
        client = mockClientRepository.save(client);

        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .id(1L)
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

        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .id(id)
                        .mealFood(mealFood)
                        .mealType(mealType)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .mealTime(mealTime)
                        .mealUpdatedBy(mealUpdatedBy)
                        .currentLastUpdatedAt(
                                foodDiaryNote.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();

        when(mockFoodDiaryNoteRepository.findById(1L)).thenReturn(Optional.of(foodDiaryNote));

        // when
        foodDiaryNoteService.updateFoodDiaryNote(id, request);

        FoodDiaryNote expected =
                FoodDiaryNote.builder()
                        .id(id)
                        .client(foodDiaryNote.getClient())
                        .mealFood(mealFood)
                        .mealType(MealType.BREAKFAST)
                        .mealDrink(mealDrink)
                        .mealComments(mealComments)
                        .mealTime(
                                LocalDateTime.parse(
                                        mealTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .mealUpdatedBy(mealUpdatedBy)
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(foodDiaryNote.getLastUpdatedAt())
                        .build();

        // then
        verify(mockFoodDiaryNoteRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateFoodDiaryNote_Failure() {
        // Given
        FoodDiaryNoteRequest request =
                FoodDiaryNoteRequest.builder()
                        .mealFood("mealFood")
                        .mealType("mealType")
                        .mealDrink("mealDrink")
                        .mealComments("mealComments")
                        .mealTime("mealTime")
                        .mealUpdatedBy("mealUpdatedBy")
                        .currentLastUpdatedAt(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                        .build();

        assertThrows(
                FoodDiaryNoteServiceException.class,
                () -> {

                    // When
                    foodDiaryNoteService.updateFoodDiaryNote(1L, request);
                });
    }

    @SneakyThrows
    @Test
    void testGetParticularFoodDiaryNote_Success() {
        // Given
        String date = "2021-09-09";
        Client client = Client.builder().id(1L).name("client").build();
        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .id(1L)
                        .client(client)
                        .mealType(MealType.EVE_SNACK)
                        .mealTime(LocalDateTime.now())
                        .mealFood("sandwich")
                        .mealDrink("juice")
                        .mealComments("healthy")
                        .mealUpdatedBy("issa")
                        .reportDate(LocalDate.parse(date))
                        .lastUpdatedAt(context.now())
                        .build();

        ParticularFoodDiaryNoteRequest request =
                ParticularFoodDiaryNoteRequest.builder()
                        .clientId(client.getId())
                        .reportDate(date)
                        .mealType("EVE_SNACK")
                        .build();

        when(mockFoodDiaryNoteRepository.findByClient_IdAndMealTypeAndReportDate(
                        1L, foodDiaryNote.getMealType(), LocalDate.parse(date)))
                .thenReturn(foodDiaryNote);
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        FoodDiaryNoteDTO expected = new ModelMapper().map(foodDiaryNote, FoodDiaryNoteDTO.class);

        // When
        FoodDiaryNoteDTO actual = foodDiaryNoteService.getParticularFoodDiaryNote(request);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetParticularFoodDiaryNote_Failure_ClientNotFound() {
        // Given
        long id = 1;
        ParticularFoodDiaryNoteRequest request =
                ParticularFoodDiaryNoteRequest.builder()
                        .clientId(id)
                        .mealType("BREAKFAST")
                        .reportDate("2021-09-09")
                        .build();

        assertThrows(
                FoodDiaryNoteServiceException.class,
                () -> {

                    // When
                    foodDiaryNoteService.getParticularFoodDiaryNote(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetParticularFoodDiaryNote_Failure_FoodDiaryNotFound() {
        // Given
        long id = 1;
        Client client = Client.builder().name("clientName").id(id).build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        ParticularFoodDiaryNoteRequest request =
                ParticularFoodDiaryNoteRequest.builder()
                        .clientId(id)
                        .mealType("BREAKFAST")
                        .reportDate("2021-09-09")
                        .build();

        assertThrows(
                FoodDiaryNoteServiceException.class,
                () -> {

                    // When
                    foodDiaryNoteService.getParticularFoodDiaryNote(request);
                });
    }
}
