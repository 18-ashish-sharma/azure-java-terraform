package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.HandoverSummaryServiceException;
import com.onedoorway.project.model.HandoverShift;
import com.onedoorway.project.model.HandoverSummary;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HandoverSummaryRepository;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.hamcrest.core.AnyOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class HandoverSummaryServiceTest {
    @Mock HandoverSummaryRepository mockHandoverSummaryRepository;
    @Mock HouseRepository mockHouseRepository;
    @Mock UserRepository mockUserRepository;
    private HandoverSummaryService handoverSummaryService;
    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        handoverSummaryService =
                new HandoverSummaryService(
                        mockHandoverSummaryRepository,
                        mockHouseRepository,
                        mockUserRepository,
                        context);
    }

    @SneakyThrows
    @Test
    void testCreateHandoverSummary_Success() {
        // Given
        String handoverDate = "2021-09-09";
        String handoverTime = "2021-09-09 09:00:00";
        long handoverToId = 1;
        long handoverById = 2;
        String toiletingSummary = "summary";
        String handoverShift = "MORNING";
        String behaviourSummary = "behaviour";
        String foodSummary = "food";
        String sleepSummary = "sleep";
        String activitiesSummary = "activities";
        String comments = "comments";
        String communications = "communication";
        String peopleAttended = "few";
        String placesVisited = "few";
        String topPriorities = "priority";
        String thingsLater = "things";

        HandoverSummaryRequest request =
                HandoverSummaryRequest.builder()
                        .houseCode("101")
                        .handoverDate(handoverDate)
                        .handoverTime(handoverTime)
                        .handoverToId(handoverToId)
                        .toiletingSummary(toiletingSummary)
                        .handoverShift(handoverShift)
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .activitiesSummary(activitiesSummary)
                        .comments(comments)
                        .communications(communications)
                        .peopleAttended(peopleAttended)
                        .placesVisited(placesVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .build();

        User user = User.builder().email(context.currentUser()).id(handoverById).build();
        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);

        User user1 = User.builder().email("test1@test.com").id(handoverToId).build();
        when(mockUserRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

        House house = House.builder().id(1L).houseCode("101").build();
        when(mockHouseRepository.getByHouseCode(house.getHouseCode())).thenReturn(house);

        // When
        handoverSummaryService.createHandoverSummary(request);

        // Then
        HandoverSummary expected =
                HandoverSummary.builder()
                        .house(house)
                        .handoverDate(LocalDate.parse(handoverDate))
                        .handoverTime(
                                LocalDateTime.parse(
                                        handoverTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .handoverShift(HandoverShift.valueOf(handoverShift))
                        .handoverToId(user1)
                        .handoverById(user)
                        .comments(comments)
                        .deleted(false)
                        .communications(communications)
                        .topPriorities(topPriorities)
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .activitiesSummary(activitiesSummary)
                        .toiletingSummary(toiletingSummary)
                        .placesVisited(placesVisited)
                        .peopleAttended(peopleAttended)
                        .thingsLater(thingsLater)
                        .lastUpdatedAt(context.now())
                        .build();

        ArgumentCaptor<HandoverSummary> handoverSummaryArgumentCaptor =
                ArgumentCaptor.forClass(HandoverSummary.class);
        verify(mockHandoverSummaryRepository).save(handoverSummaryArgumentCaptor.capture());
        HandoverSummary actual = handoverSummaryArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @Test
    void testCreateHandoverSummary_Failure_HouseNotFound() {
        // Given
        String handoverDate = "2021-09-09";
        String handoverTime = "2021-09-09 09:00:00";
        long handoverToId = 1;
        String toiletingSummary = "summary";
        String handoverShift = "MORNING";
        String behaviourSummary = "behaviour";
        String foodSummary = "food";
        String sleepSummary = "sleep";
        String activitiesSummary = "activities";
        String comments = "comments";
        String communications = "communication";
        String peopleAttended = "few";
        String placesVisited = "few";
        String topPriorities = "priority";
        String thingsLater = "things";

        HandoverSummaryRequest request =
                HandoverSummaryRequest.builder()
                        .houseCode("101")
                        .handoverDate(handoverDate)
                        .handoverTime(handoverTime)
                        .handoverToId(handoverToId)
                        .toiletingSummary(toiletingSummary)
                        .handoverShift(handoverShift)
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .activitiesSummary(activitiesSummary)
                        .comments(comments)
                        .communications(communications)
                        .peopleAttended(peopleAttended)
                        .placesVisited(placesVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .build();

        assertThrows(
                HandoverSummaryServiceException.class,
                () -> {
                    // When
                    handoverSummaryService.createHandoverSummary(request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListHandoverSummary_Success() {
        // Given
        House house = House.builder().id(1L).houseCode("103").build();
        User user1 = User.builder().email("gettest@test.com").password("test").build();
        User user2 = User.builder().email("gettester@test.com").password("test").build();
        HandoverSummary handoverSummary =
                HandoverSummary.builder()
                        .id(1L)
                        .house(house)
                        .handoverDate(LocalDate.now())
                        .handoverTime(LocalDateTime.now())
                        .handoverById(user1)
                        .handoverToId(user2)
                        .handoverShift(HandoverShift.MORNING)
                        .behaviourSummary("behaviour")
                        .sleepSummary("sleep")
                        .toiletingSummary("toilet")
                        .activitiesSummary("activities")
                        .deleted(false)
                        .communications("communication")
                        .comments("comments")
                        .peopleAttended("few")
                        .placesVisited("few")
                        .thingsLater("things")
                        .lastUpdatedAt(context.now())
                        .build();
        when(mockHandoverSummaryRepository.findAllByHouse_HouseCodeAndHandoverDateBetween(
                        any(), any(), any(), any()))
                .thenReturn(List.of(handoverSummary));
        ListHandoverSummaryRequest request =
                ListHandoverSummaryRequest.builder()
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .houseCode(house.getHouseCode())
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        List<HandoverSummaryDTO> handoverSummaryDTOS =
                handoverSummaryService.listHandoverSummary(request);

        // Then
        assertEquals(handoverSummaryDTOS.size(), 1);
        assertThat(
                handoverSummaryDTOS,
                (allOf(
                        everyItem(hasProperty("id", AnyOf.anyOf(equalTo(1L)))),
                        everyItem(
                                hasProperty(
                                        "houseCode",
                                        AnyOf.anyOf(
                                                equalTo(
                                                        handoverSummary
                                                                .getHouse()
                                                                .getHouseCode())))),
                        everyItem(
                                hasProperty(
                                        "handoverDate",
                                        AnyOf.anyOf(equalTo(handoverSummary.getHandoverDate())))),
                        everyItem(
                                hasProperty(
                                        "handoverTime",
                                        AnyOf.anyOf(equalTo(handoverSummary.getHandoverTime())))),
                        everyItem(hasProperty("handoverShift", AnyOf.anyOf(equalTo("MORNING")))),
                        everyItem(
                                hasProperty(
                                        "handoverBy",
                                        AnyOf.anyOf(
                                                equalTo(
                                                        UserDTO.builder()
                                                                .id(user1.getId())
                                                                .email(user1.getEmail())
                                                                .build())))),
                        everyItem(
                                hasProperty(
                                        "handoverTo",
                                        AnyOf.anyOf(
                                                equalTo(
                                                        UserDTO.builder()
                                                                .id(user2.getId())
                                                                .email(user2.getEmail())
                                                                .build())))),
                        everyItem(
                                hasProperty(
                                        "deleted",
                                        AnyOf.anyOf(equalTo(handoverSummary.getDeleted())))),
                        everyItem(
                                hasProperty(
                                        "behaviourSummary",
                                        AnyOf.anyOf(
                                                equalTo(handoverSummary.getBehaviourSummary())))),
                        everyItem(
                                hasProperty(
                                        "sleepSummary",
                                        AnyOf.anyOf(equalTo(handoverSummary.getSleepSummary())))),
                        everyItem(
                                hasProperty(
                                        "foodSummary",
                                        AnyOf.anyOf(equalTo(handoverSummary.getFoodSummary())))),
                        everyItem(
                                hasProperty(
                                        "toiletingSummary",
                                        AnyOf.anyOf(
                                                equalTo(handoverSummary.getToiletingSummary())))),
                        everyItem(
                                hasProperty(
                                        "activitiesSummary",
                                        AnyOf.anyOf(
                                                equalTo(handoverSummary.getActivitiesSummary())))),
                        everyItem(
                                hasProperty(
                                        "communications" + "",
                                        AnyOf.anyOf(equalTo(handoverSummary.getCommunications())))),
                        everyItem(
                                hasProperty(
                                        "topPriorities",
                                        AnyOf.anyOf(equalTo(handoverSummary.getTopPriorities())))),
                        everyItem(
                                hasProperty(
                                        "comments",
                                        AnyOf.anyOf(equalTo(handoverSummary.getComments())))),
                        everyItem(
                                hasProperty(
                                        "peopleAttended",
                                        AnyOf.anyOf(equalTo(handoverSummary.getPeopleAttended())))),
                        everyItem(
                                hasProperty(
                                        "placesVisited",
                                        AnyOf.anyOf(equalTo(handoverSummary.getPlacesVisited())))),
                        everyItem(
                                hasProperty(
                                        "thingsLater",
                                        AnyOf.anyOf(equalTo(handoverSummary.getThingsLater())))),
                        everyItem(
                                hasProperty(
                                        "lastUpdatedAt", AnyOf.anyOf(equalTo(context.now())))))));
    }

    @Test
    void testListAllHandoverSummary_Success_Empty() {
        // Given
        long id = 1;
        ListHandoverSummaryRequest request =
                ListHandoverSummaryRequest.builder()
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .houseCode("111")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        List<HandoverSummaryDTO> handoverSummaryDTOS =
                handoverSummaryService.listHandoverSummary(request);

        // Then
        assertEquals(handoverSummaryDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateHandoverSummary_Success() {
        // Given
        long id = 1L;
        String handoverDate = "2021-09-09";
        String handoverTime = "2021-09-09 09:00:00";
        String toiletingSummary = "summary";
        String handoverShift = "MORNING";
        String behaviourSummary = "behaviour";
        String foodSummary = "food";
        String sleepSummary = "sleep";
        String activitiesSummary = "activities";
        String comments = "comments";
        String communications = "communication";
        String peopleAttended = "few";
        String placesVisited = "few";
        String topPriorities = "priority";
        String thingsLater = "things";

        House house = House.builder().id(1L).houseCode("100").build();
        house = mockHouseRepository.save(house);
        User user1 = User.builder().id(1).email("tom@test.com").password("password").build();
        mockUserRepository.save(user1);
        User user2 = User.builder().id(2).email("tom1@test.com").password("password").build();
        mockUserRepository.save(user2);

        HandoverSummary handoverSummary =
                HandoverSummary.builder()
                        .house(house)
                        .handoverDate(LocalDate.now())
                        .handoverTime(LocalDateTime.now())
                        .handoverById(user1)
                        .handoverToId(user2)
                        .toiletingSummary("summary")
                        .handoverShift(HandoverShift.MORNING)
                        .behaviourSummary("behaviour")
                        .foodSummary("food")
                        .sleepSummary("sleep")
                        .activitiesSummary("activities")
                        .comments("comments")
                        .deleted(false)
                        .communications("communications")
                        .peopleAttended("few")
                        .placesVisited("few")
                        .topPriorities("Priorities")
                        .thingsLater("things")
                        .lastUpdatedAt(context.now())
                        .build();

        UpdateHandoverSummaryRequest updatehandoverSummaryRequest =
                UpdateHandoverSummaryRequest.builder()
                        .handoverDate(handoverDate)
                        .handoverTime(handoverTime)
                        .handoverToId(user2.getId())
                        .toiletingSummary(toiletingSummary)
                        .handoverShift(handoverShift)
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .activitiesSummary(activitiesSummary)
                        .comments(comments)
                        .communications(communications)
                        .deleted(false)
                        .peopleAttended(peopleAttended)
                        .placesVisited(placesVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .build();

        when(mockHandoverSummaryRepository.findById(1L)).thenReturn(Optional.of(handoverSummary));
        when(mockUserRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(mockUserRepository.getByEmail("test@test.com")).thenReturn(user1);

        // When
        handoverSummaryService.updateHandoverSummary(id, updatehandoverSummaryRequest);

        HandoverSummary expected =
                HandoverSummary.builder()
                        .house(handoverSummary.getHouse())
                        .handoverDate(handoverSummary.getHandoverDate())
                        .handoverTime(
                                LocalDateTime.parse(
                                        handoverTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .handoverById(handoverSummary.getHandoverById())
                        .handoverToId(handoverSummary.getHandoverToId())
                        .toiletingSummary(toiletingSummary)
                        .handoverShift(HandoverShift.MORNING)
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .activitiesSummary(activitiesSummary)
                        .comments(comments)
                        .communications(communications)
                        .deleted(false)
                        .peopleAttended(peopleAttended)
                        .placesVisited(placesVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .lastUpdatedAt(handoverSummary.getLastUpdatedAt())
                        .build();

        // Then
        verify(mockHandoverSummaryRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateHandoverSummary_Failure_HandoverSummaryNotFound() {
        // Given
        long handoverToId = 1;
        String handoverDate = "2021-09-09";
        String handoverTime = "2021-09-09 09:00:00";
        String toiletingSummary = "summary";
        String handoverShift = "MORNING";
        String behaviourSummary = "behaviour";
        String foodSummary = "food";
        String sleepSummary = "sleep";
        String activitiesSummary = "activities";
        String comments = "comments";
        String communications = "communication";
        String peopleAttended = "few";
        String placesVisited = "few";
        String topPriorities = "priority";
        String thingsLater = "things";

        UpdateHandoverSummaryRequest updatehandoverSummaryRequest =
                UpdateHandoverSummaryRequest.builder()
                        .handoverDate(handoverDate)
                        .handoverTime(handoverTime)
                        .handoverToId(handoverToId)
                        .toiletingSummary(toiletingSummary)
                        .handoverShift(handoverShift)
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .activitiesSummary(activitiesSummary)
                        .comments(comments)
                        .communications(communications)
                        .deleted(false)
                        .peopleAttended(peopleAttended)
                        .placesVisited(placesVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .build();

        assertThrows(
                HandoverSummaryServiceException.class,
                () -> {
                    // When
                    handoverSummaryService.updateHandoverSummary(1L, updatehandoverSummaryRequest);
                });
    }
}
