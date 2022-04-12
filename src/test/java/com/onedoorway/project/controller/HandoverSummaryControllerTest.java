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
import com.onedoorway.project.dto.HandoverSummaryRequest;
import com.onedoorway.project.dto.ListHandoverSummaryRequest;
import com.onedoorway.project.dto.UpdateHandoverSummaryRequest;
import com.onedoorway.project.dto.UserDTO;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.HandoverSummaryRepository;
import com.onedoorway.project.repository.HouseRepository;
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
import java.util.List;
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
class HandoverSummaryControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private HandoverSummaryRepository handoverSummaryRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private HouseRepository houseRepository;

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
        handoverSummaryRepository.deleteAll();
        houseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create hand over summary")
    void testCreateHandoverSummarySuccess() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).id(1).build();
        house = houseRepository.save(house);

        User user = User.builder().id(1).email("tom@test.com").password("password").build();
        user = userRepository.save(user);

        HandoverSummaryRequest request =
                HandoverSummaryRequest.builder()
                        .houseCode(house.getHouseCode())
                        .handoverDate("2021-09-09")
                        .handoverTime("2021-09-09 09:00:00")
                        .handoverToId(user.getId())
                        .toiletingSummary("summary")
                        .handoverShift("MORNING")
                        .behaviourSummary("behaviour")
                        .foodSummary("food")
                        .sleepSummary("sleep")
                        .activitiesSummary("activities")
                        .comments("comments")
                        .communications("communication")
                        .peopleAttended("few")
                        .placesVisited("few")
                        .topPriorities("priority")
                        .thingsLater("things")
                        .build();

        // When
        mockMvc.perform(
                        post("/handover/create")
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
    @DisplayName("POST /create hand over summary without fields")
    void testCreateHandoverSummarySuccess_WithoutFields() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).id(1).build();
        house = houseRepository.save(house);

        User user = User.builder().id(1).email("tom@test.com").password("password").build();
        user = userRepository.save(user);

        HandoverSummaryRequest request =
                HandoverSummaryRequest.builder()
                        .houseCode(house.getHouseCode())
                        .handoverDate("2021-09-09")
                        .handoverTime("2021-09-09 09:00:00")
                        .handoverToId(user.getId())
                        .handoverShift("MORNING")
                        .build();

        // When
        mockMvc.perform(
                        post("/handover/create")
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
    @DisplayName("POST /create hand over summary failure")
    void testCreateHandoverSummaryFailure() {
        // Given
        HandoverSummaryRequest request =
                HandoverSummaryRequest.builder()
                        .houseCode("10")
                        .handoverDate("2021-09-09")
                        .handoverTime("2021-09-09 09:00:00")
                        .handoverToId(1)
                        .handoverShift("MORNING")
                        .build();

        // When
        mockMvc.perform(
                        post("/handover/create")
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
    @DisplayName("GET / get all Handover Summary")
    void testListHandoverSummary() {
        // Given
        long id = 1;
        House house = House.builder().id(id).houseCode("103").build();
        house = houseRepository.save(house);

        User user1 = User.builder().email("gettest@test.com").password("test").build();
        user1 = userRepository.save(user1);

        User user2 = User.builder().email("gettester@test.com").password("test").build();
        user2 = userRepository.save(user2);

        HandoverSummary handoverSummary =
                HandoverSummary.builder()
                        .id(1)
                        .house(house)
                        .handoverDate(LocalDate.parse("2021-11-18"))
                        .handoverTime(LocalDateTime.now())
                        .handoverShift(HandoverShift.MORNING)
                        .handoverById(user1)
                        .handoverToId(user2)
                        .behaviourSummary("Behaviour okay")
                        .sleepSummary("slept till 6.30 am")
                        .foodSummary("had a healthy brunch")
                        .deleted(false)
                        .toiletingSummary("no problem")
                        .activitiesSummary("had a warm up session")
                        .communications(
                                "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis")
                        .topPriorities(
                                "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia volupta")
                        .comments(
                                "Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their")
                        .peopleAttended("a few")
                        .placesVisited("school")
                        .thingsLater("sleep")
                        .lastUpdatedAt(Instant.now())
                        .build();
        handoverSummary = handoverSummaryRepository.save(handoverSummary);

        ListHandoverSummaryRequest request =
                ListHandoverSummaryRequest.builder()
                        .houseCode(house.getHouseCode())
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        mockMvc.perform(
                        post("/handover/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(handoverSummary.getId()))
                .andExpect(
                        jsonPath("$.[0].houseCode")
                                .value(handoverSummary.getHouse().getHouseCode()))
                .andExpect(
                        jsonPath("$.[0].handoverDate")
                                .value(
                                        handoverSummary
                                                .getHandoverDate()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.[0].handoverTime")
                                .value(
                                        handoverSummary
                                                .getHandoverTime()
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(
                        jsonPath("$.[0].handoverShift")
                                .value(handoverSummary.getHandoverShift().name()))
                .andExpect(jsonPath("$.[0].deleted").value(handoverSummary.getDeleted()))
                .andExpect(
                        jsonPath("$.[0].handoverBy")
                                .value(
                                        UserDTO.builder()
                                                .id(user1.getId())
                                                .email(user1.getEmail())
                                                .firstName(user1.getFirstName())
                                                .lastName(user1.getLastName())
                                                .roleNames(null)
                                                .houses(List.of())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].handoverTo")
                                .value(
                                        UserDTO.builder()
                                                .id(user2.getId())
                                                .email(user2.getEmail())
                                                .firstName(user2.getFirstName())
                                                .lastName(user2.getLastName())
                                                .roleNames(null)
                                                .houses(List.of())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].behaviourSummary")
                                .value(handoverSummary.getBehaviourSummary()))
                .andExpect(jsonPath("$.[0].sleepSummary").value(handoverSummary.getSleepSummary()))
                .andExpect(jsonPath("$.[0].foodSummary").value(handoverSummary.getFoodSummary()))
                .andExpect(
                        jsonPath("$.[0].toiletingSummary")
                                .value(handoverSummary.getToiletingSummary()))
                .andExpect(
                        jsonPath("$.[0].activitiesSummary")
                                .value(handoverSummary.getActivitiesSummary()))
                .andExpect(
                        jsonPath("$.[0].communications").value(handoverSummary.getCommunications()))
                .andExpect(
                        jsonPath("$.[0].topPriorities").value(handoverSummary.getTopPriorities()))
                .andExpect(jsonPath("$.[0].comments").value(handoverSummary.getComments()))
                .andExpect(
                        jsonPath("$.[0].peopleAttended").value(handoverSummary.getPeopleAttended()))
                .andExpect(
                        jsonPath("$.[0].placesVisited").value(handoverSummary.getPlacesVisited()))
                .andExpect(jsonPath("$.[0].thingsLater").value(handoverSummary.getThingsLater()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        handoverSummary
                                                .getLastUpdatedAt()
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update handover summary success")
    void testUpdateHandoverSummary_Success() {
        // Given
        String handoverDate = "2021-11-25";
        String handoverTime = "2021-11-25 16:00:00";
        String handoverShift = "MORNING";
        String behaviourSummary = "behaviour";
        String foodSummary = "food";
        String sleepSummary = "sleep";
        String toiletingSummary = "summary";
        String activitiesSummary = "activities";
        String communications = "communications";
        String comments = "comments";
        String peopleAttended = "few";
        String peopleVisited = "few";
        String topPriorities = "priorities";
        String thingsLater = "things";
        House house = House.builder().id(1).houseCode("100").build();
        house = houseRepository.save(house);

        User user1 = User.builder().id(1).email("tom@test.com").password("password").build();
        user1 = userRepository.save(user1);
        User user2 = User.builder().id(2).email("gettester@test.com").password("test").build();
        user2 = userRepository.save(user2);

        HandoverSummary handoverSummary =
                HandoverSummary.builder()
                        .id(1)
                        .house(house)
                        .handoverDate(LocalDate.now())
                        .handoverTime(LocalDateTime.now())
                        .handoverShift(HandoverShift.MORNING)
                        .handoverById(user1)
                        .handoverToId(user2)
                        .behaviourSummary("behaviour")
                        .foodSummary("food")
                        .sleepSummary("sleep")
                        .toiletingSummary("summary")
                        .activitiesSummary("activities")
                        .communications("communications")
                        .deleted(false)
                        .comments("comments")
                        .peopleAttended("few")
                        .placesVisited("few")
                        .topPriorities("Priorities")
                        .thingsLater("things")
                        .build();
        handoverSummary = handoverSummaryRepository.save(handoverSummary);

        UpdateHandoverSummaryRequest request =
                UpdateHandoverSummaryRequest.builder()
                        .handoverDate(handoverDate)
                        .handoverTime(handoverTime)
                        .handoverShift(handoverShift)
                        .handoverToId(user2.getId())
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .toiletingSummary(toiletingSummary)
                        .activitiesSummary(activitiesSummary)
                        .communications(communications)
                        .comments(comments)
                        .deleted(false)
                        .peopleAttended(peopleAttended)
                        .placesVisited(peopleVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .build();
        // when
        mockMvc.perform(
                        put("/handover/update/{id}", handoverSummary.getId())
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
    @DisplayName("PUT /update handover summary failure")
    void testUpdateHandoverSummary_Failure() {
        // Given
        long handoverToId = 1L;
        String handoverDate = "2021-11-25";
        String handoverTime = "2021-11-25 16:00:00";
        String handoverShift = "MORNING";
        String behaviourSummary = "behaviour";
        String foodSummary = "food";
        String sleepSummary = "sleep";
        String toiletingSummary = "summary";
        String activitiesSummary = "activities";
        String communications = "communications";
        String comments = "comments";
        String peopleAttended = "few";
        String peopleVisited = "few";
        String topPriorities = "priorities";
        String thingsLater = "things";

        User user = User.builder().id(2).email("gettester@test.com").password("test").build();
        user = userRepository.save(user);

        UpdateHandoverSummaryRequest request =
                UpdateHandoverSummaryRequest.builder()
                        .handoverDate(handoverDate)
                        .handoverTime(handoverTime)
                        .handoverShift(handoverShift)
                        .handoverToId(user.getId())
                        .behaviourSummary(behaviourSummary)
                        .foodSummary(foodSummary)
                        .sleepSummary(sleepSummary)
                        .toiletingSummary(toiletingSummary)
                        .activitiesSummary(activitiesSummary)
                        .communications(communications)
                        .deleted(false)
                        .comments(comments)
                        .peopleAttended(peopleAttended)
                        .placesVisited(peopleVisited)
                        .topPriorities(topPriorities)
                        .thingsLater(thingsLater)
                        .build();
        // When
        mockMvc.perform(
                        put("/handover/update/{id}", 300)
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
