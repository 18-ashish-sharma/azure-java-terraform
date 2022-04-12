package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.onedoorway.project.dto.DailyNoteDTO;
import com.onedoorway.project.dto.DailyNoteRequest;
import com.onedoorway.project.dto.SearchDailyNoteRequest;
import com.onedoorway.project.dto.UpdateDailyNoteRequest;
import com.onedoorway.project.exception.DailyNoteServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.DailyNote;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.DailyNoteRepository;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.exparity.hamcrest.date.DateMatchers;
import org.hamcrest.beans.HasPropertyWithValue;
import org.hamcrest.core.AnyOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class DailyNoteServiceTest {

    @Mock DailyNoteRepository mockDailyNoteRepository;

    @Mock HouseRepository mockHouseRepository;

    @Mock UserRepository mockUserRepository;

    @Mock ClientRepository mockClientRepository;

    private DailyNoteService dailyNoteService;

    @BeforeEach
    void init() {
        dailyNoteService =
                new DailyNoteService(
                        mockDailyNoteRepository,
                        mockHouseRepository,
                        mockUserRepository,
                        mockClientRepository);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testCreateDailyNotes_Success() {
        // Given
        String houseCode = "101";
        String note = "test";
        String dob = "1957-01-01";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        DailyNoteRequest request =
                DailyNoteRequest.builder()
                        .note(note)
                        .clientId(1L)
                        .houseCode(houseCode)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        House house = House.builder().houseCode(houseCode).id(1).build();
        User user = User.builder().email("test@test.com").id(1).build();
        Client client =
                Client.builder()
                        .name("name")
                        .gender("gender")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("email")
                        .phone("phone")
                        .addrLine1("address1")
                        .addrLine2("address2")
                        .city("city")
                        .state("state")
                        .postCode("postCode")
                        .build();

        when(mockUserRepository.getByEmail("user")).thenReturn(user);
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        dailyNoteService.createDailyNote(request);

        // Then
        DailyNote expected =
                DailyNote.builder()
                        .note(note)
                        .house(house)
                        .client(client)
                        .startTime(
                                LocalDateTime.parse(
                                        startTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .endTime(
                                LocalDateTime.parse(
                                        endTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .createBy(user)
                        .createdAt(Instant.now())
                        .build();

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<DailyNote> dailyNoteArgumentCaptor =
                ArgumentCaptor.forClass(DailyNote.class);
        verify(mockDailyNoteRepository).save(dailyNoteArgumentCaptor.capture());
        DailyNote actual = dailyNoteArgumentCaptor.getValue();

        assertThat(
                Date.from(expected.getCreatedAt()),
                DateMatchers.within(
                        5,
                        ChronoUnit.SECONDS,
                        LocalDateTime.ofInstant(
                                actual.getCreatedAt(), ZoneOffset.systemDefault())));
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("note", equalTo(note)),
                        HasPropertyWithValue.hasProperty("createBy", equalTo(user)),
                        HasPropertyWithValue.hasProperty(
                                "startTime",
                                equalTo(
                                        LocalDateTime.parse(
                                                startTime,
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss")))),
                        HasPropertyWithValue.hasProperty(
                                "endTime",
                                equalTo(
                                        LocalDateTime.parse(
                                                endTime,
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss")))),
                        HasPropertyWithValue.hasProperty("house", equalTo(house)))));
    }

    @Test
    @WithMockUser
    void testCreateDailyNotes_Failure_NoUserFound() {
        // Given
        String houseCode = "101";
        String note = "test";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        DailyNoteRequest request =
                DailyNoteRequest.builder()
                        .note(note)
                        .clientId(1L)
                        .houseCode(houseCode)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        assertThrows(
                DailyNoteServiceException.class, () -> dailyNoteService.createDailyNote(request));
    }

    @Test
    void testCreateDailyNotes_Failure_UnAuthenticated() {
        // Given
        String houseCode = "101";
        String note = "test";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        DailyNoteRequest request =
                DailyNoteRequest.builder()
                        .note(note)
                        .clientId(1L)
                        .houseCode(houseCode)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        assertThrows(
                DailyNoteServiceException.class, () -> dailyNoteService.createDailyNote(request));
    }

    @Test
    @WithMockUser
    void testCreateDailyNotes_Failure_NoHouseFound() {
        // Given
        String houseCode = "101";
        String note = "test";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        DailyNoteRequest request =
                DailyNoteRequest.builder()
                        .note(note)
                        .clientId(1L)
                        .houseCode(houseCode)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        User user = User.builder().email("test@test.com").id(1).build();
        when(mockUserRepository.getByEmail("user")).thenReturn(user);

        // When
        assertThrows(
                DailyNoteServiceException.class, () -> dailyNoteService.createDailyNote(request));
    }

    @SneakyThrows
    @Test
    void testGetDailyNotes() {
        String houseCode = "101";
        User user =
                User.builder()
                        .id(1)
                        .email("email@gmail.com")
                        .firstName("first")
                        .lastName("last")
                        .build();
        House house = House.builder().houseCode(houseCode).id(1).build();
        String note = "test";
        Instant now = Instant.now();
        String dob = "1957-01-01";
        LocalDateTime startTime1 =
                LocalDateTime.parse(
                        "2021-09-10 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime1 =
                LocalDateTime.parse(
                        "2021-09-10 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime startTime2 =
                LocalDateTime.parse(
                        "2021-09-11 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime2 =
                LocalDateTime.parse(
                        "2021-09-12 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Client client =
                Client.builder()
                        .name("name")
                        .gender("gender")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("email")
                        .phone("phone")
                        .addrLine1("address1")
                        .addrLine2("address2")
                        .city("city")
                        .state("state")
                        .postCode("postCode")
                        .build();

        when(mockDailyNoteRepository.findByHouse_HouseCodeAndCreateBy_Id(
                        any(String.class), any(Long.class)))
                .thenReturn(
                        List.of(
                                DailyNote.builder()
                                        .house(house)
                                        .note(note)
                                        .client(client)
                                        .id(1)
                                        .startTime(startTime1)
                                        .endTime(endTime1)
                                        .createBy(user)
                                        .createdAt(now)
                                        .build(),
                                DailyNote.builder()
                                        .house(house)
                                        .note(note)
                                        .client(client)
                                        .id(2)
                                        .startTime(startTime2)
                                        .endTime(endTime2)
                                        .createBy(user)
                                        .createdAt(now)
                                        .build()));

        List<DailyNoteDTO> dailyNotes = dailyNoteService.getDailyNotes(houseCode, 1);

        assertEquals(dailyNotes.size(), 2);
        assertThat(
                dailyNotes,
                (allOf(
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "id", AnyOf.anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "note", AnyOf.anyOf(equalTo(note), equalTo(note)))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createById",
                                        AnyOf.anyOf(equalTo(user.getId()), equalTo(user.getId())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createByEmail",
                                        AnyOf.anyOf(
                                                equalTo(user.getEmail()),
                                                equalTo(user.getEmail())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createByFirstName",
                                        AnyOf.anyOf(
                                                equalTo(user.getFirstName()),
                                                equalTo(user.getFirstName())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createByLastName",
                                        AnyOf.anyOf(
                                                equalTo(user.getLastName()),
                                                equalTo(user.getLastName())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "startTime",
                                        AnyOf.anyOf(equalTo(startTime1), equalTo(startTime2)))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "endTime",
                                        AnyOf.anyOf(equalTo(endTime1), equalTo(endTime2)))),
                        everyItem(HasPropertyWithValue.hasProperty("createdAt", equalTo(now))))));
    }

    @Test
    @SneakyThrows
    void testSearch() {
        String houseCode = "101";
        User user =
                User.builder()
                        .id(1)
                        .email("workmail@gmail.com")
                        .firstName("first")
                        .lastName("last")
                        .build();
        House house = House.builder().houseCode(houseCode).id(1).build();
        String note = "test";
        Instant now = Instant.now();
        String dob = "1957-01-01";
        LocalDateTime startTime1 =
                LocalDateTime.parse(
                        "2021-09-10 10:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime1 =
                LocalDateTime.parse(
                        "2021-09-10 12:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Client client =
                Client.builder()
                        .name("name")
                        .gender("gender")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("email")
                        .phone("phone")
                        .addrLine1("address1")
                        .addrLine2("address2")
                        .city("city")
                        .state("state")
                        .postCode("postCode")
                        .build();

        when(mockDailyNoteRepository.findAllByHouse_HouseCodeAndClient_IdAndCreatedAtBetween(
                        any(String.class),
                        any(Long.class),
                        any(Instant.class),
                        any(Instant.class),
                        any(Pageable.class)))
                .thenReturn(
                        List.of(
                                DailyNote.builder()
                                        .house(house)
                                        .note(note)
                                        .id(1)
                                        .client(client)
                                        .startTime(startTime1)
                                        .endTime(endTime1)
                                        .createBy(user)
                                        .createdAt(now)
                                        .build(),
                                DailyNote.builder()
                                        .house(house)
                                        .note(note)
                                        .id(2)
                                        .client(client)
                                        .createBy(user)
                                        .startTime(startTime1)
                                        .endTime(endTime1)
                                        .createdAt(now)
                                        .build()));

        SearchDailyNoteRequest request =
                SearchDailyNoteRequest.builder()
                        .houseCode(houseCode)
                        .clientId(1L)
                        .start(Instant.now())
                        .end(Instant.now())
                        .pageNumber(0)
                        .pageSize(2)
                        .build();
        List<DailyNoteDTO> dailyNotes = dailyNoteService.search(request);

        assertEquals(dailyNotes.size(), 2);
        assertThat(
                dailyNotes,
                (allOf(
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "id", AnyOf.anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(HasPropertyWithValue.hasProperty("note", equalTo("test"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createById", equalTo(user.getId()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createByEmail", equalTo(user.getEmail()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createByFirstName", equalTo(user.getFirstName()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createByLastName", equalTo(user.getLastName()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("startTime", equalTo(startTime1))),
                        everyItem(HasPropertyWithValue.hasProperty("endTime", equalTo(endTime1))),
                        everyItem(HasPropertyWithValue.hasProperty("createdAt", equalTo(now))))));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testDeleteDailyNote_Success() {
        // Given
        long id = 1;
        House house = House.builder().houseCode("102").id(1).build();
        User user = User.builder().email("test@test.com").id(1).build();
        DailyNote dailyNote =
                DailyNote.builder()
                        .id(1)
                        .note("note")
                        .house(house)
                        .createBy(user)
                        .createdAt(Instant.now())
                        .build();
        when(mockDailyNoteRepository.findById(id)).thenReturn(Optional.of(dailyNote));

        // When
        dailyNoteService.deleteDailyNote(id);

        // Then
        // capture what was about to be deleted and make sure that is as expected
        ArgumentCaptor<DailyNote> dailyNoteArgumentCaptor =
                ArgumentCaptor.forClass(DailyNote.class);
        verify(mockDailyNoteRepository).delete(dailyNoteArgumentCaptor.capture());
        DailyNote actual = dailyNoteArgumentCaptor.getValue();
        assertEquals(dailyNote, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testDeleteDailyNote_Success_Not_exists() {
        // Given
        long id = 1;
        when(mockDailyNoteRepository.findById(id)).thenReturn(Optional.empty());

        // When
        dailyNoteService.deleteDailyNote(id);

        // Then
        // verify that the invocation to delete has not happened
        verify(mockDailyNoteRepository, times(0)).delete(any(DailyNote.class));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateDailyNotes_Success() {
        // Given
        String note = "updated";
        long id = 1;
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        House house = House.builder().houseCode("101").build();
        house = mockHouseRepository.save(house);
        Instant createdAt = Instant.now();
        User user = User.builder().email("test@test.com").password("test").build();
        user = mockUserRepository.save(user);

        DailyNote dailyNote =
                DailyNote.builder()
                        .id(id)
                        .house(house)
                        .createBy(user)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .note("test")
                        .createdAt(createdAt)
                        .build();
        UpdateDailyNoteRequest request =
                UpdateDailyNoteRequest.builder()
                        .note(note)
                        .id(id)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        when(mockDailyNoteRepository.findById(id)).thenReturn(Optional.of(dailyNote));

        // when
        dailyNoteService.updateDailyNote(request);

        DailyNote expected =
                DailyNote.builder()
                        .id(id)
                        .house(house)
                        .createBy(user)
                        .note(note)
                        .startTime(
                                LocalDateTime.parse(
                                        startTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .endTime(
                                LocalDateTime.parse(
                                        endTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .createdAt(createdAt)
                        .build();
        // then
        verify(mockDailyNoteRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateDailyNote_Failure_DailyNoteNotFound() {
        // Given
        String note = "updated";
        long id = 1;
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        UpdateDailyNoteRequest request =
                UpdateDailyNoteRequest.builder()
                        .note(note)
                        .id(id)
                        .startTime(startTime)
                        .endTime(endTime)
                        .build();

        assertThrows(
                DailyNoteServiceException.class,
                () -> {
                    // When
                    dailyNoteService.updateDailyNote(request);
                });
    }
}
