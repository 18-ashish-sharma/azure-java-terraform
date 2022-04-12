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
import com.onedoorway.project.dto.SleepTrackerNotesDTO;
import com.onedoorway.project.dto.SleepTrackerNotesRequest;
import com.onedoorway.project.dto.UpdateSleepTrackerRequest;
import com.onedoorway.project.exception.SleepTrackerNotesServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.SleepTrackerNotes;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.SleepTrackerNotesRepository;
import java.time.Instant;
import java.time.LocalDate;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SleepTrackerNotesServiceTest {
    @Mock SleepTrackerNotesRepository mockSleepTrackerNotesRepository;

    @Mock ClientRepository mockClientRepository;

    private SleepTrackerNotesService sleepTrackerNotesService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        sleepTrackerNotesService =
                new SleepTrackerNotesService(
                        mockSleepTrackerNotesRepository, mockClientRepository, context);
    }

    @SneakyThrows
    @Test
    void testCreateSleepTrackerNotes_Success() {
        // Given
        String firstSlot = "Awake at 11:00pm";
        String firstUpdatedBy = "Helen";
        String secondSlot = "";
        String secondUpdatedBy = "Tom";
        String thirdSlot = "Awake at 1:00pm";
        String thirdUpdatedBy = "joe";
        String fourthSlot = "";
        String fourthUpdatedBy = "jess";
        String fifthSlot = "hu";
        String fifthUpdatedBy = "mary";
        String sixthSlot = "rth";
        String sixthUpdatedBy = "ram";
        String seventhSlot = "Awake at 5:00pm";
        String seventhUpdatedBy = "rob";
        String eighthSlot = "";
        String eighthUpdatedBy = "jack";
        LocalDate reportDate = LocalDate.of(2021, 10, 14);
        Instant lastUpdatedAt = context.now();

        SleepTrackerNotesRequest request =
                SleepTrackerNotesRequest.builder()
                        .clientId(1L)
                        .firstSlot(firstSlot)
                        .firstUpdatedBy(firstUpdatedBy)
                        .secondSlot(secondSlot)
                        .secondUpdatedBy(secondUpdatedBy)
                        .thirdSlot(thirdSlot)
                        .thirdUpdatedBy(thirdUpdatedBy)
                        .fourthSlot(fourthSlot)
                        .fourthUpdatedBy(fourthUpdatedBy)
                        .fifthSlot(fifthSlot)
                        .fifthUpdatedBy(fifthUpdatedBy)
                        .sixthSlot(sixthSlot)
                        .sixthUpdatedBy(sixthUpdatedBy)
                        .seventhSlot(seventhSlot)
                        .seventhUpdatedBy(seventhUpdatedBy)
                        .eighthSlot(eighthSlot)
                        .eighthUpdatedBy(eighthUpdatedBy)
                        .reportDate(reportDate)
                        .build();

        Client client =
                Client.builder()
                        .name("name")
                        .gender("gender")
                        .dob(
                                LocalDate.parse(
                                        "1985-08-07", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("email")
                        .phone("phone")
                        .addrLine1("address1")
                        .addrLine2("address2")
                        .city("city")
                        .state("state")
                        .postCode("postCode")
                        .build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When
        sleepTrackerNotesService.createSleepTrackerNotes(request);

        // Then
        SleepTrackerNotes expected =
                SleepTrackerNotes.builder()
                        .id(1L)
                        .client(client)
                        .firstSlot(firstSlot)
                        .firstUpdatedBy(firstUpdatedBy)
                        .secondSlot(secondSlot)
                        .secondUpdatedBy(secondUpdatedBy)
                        .thirdSlot(thirdSlot)
                        .thirdUpdatedBy(thirdUpdatedBy)
                        .fourthSlot(fourthSlot)
                        .fourthUpdatedBy(fourthUpdatedBy)
                        .fifthSlot(fifthSlot)
                        .fifthUpdatedBy(fifthUpdatedBy)
                        .sixthSlot(sixthSlot)
                        .sixthUpdatedBy(sixthUpdatedBy)
                        .seventhSlot(seventhSlot)
                        .seventhUpdatedBy(seventhUpdatedBy)
                        .eighthSlot(eighthSlot)
                        .eighthUpdatedBy(eighthUpdatedBy)
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();

        ArgumentCaptor<SleepTrackerNotes> sleepTrackerNotesArgumentCaptor =
                ArgumentCaptor.forClass(SleepTrackerNotes.class);
        verify(mockSleepTrackerNotesRepository).save(sleepTrackerNotesArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("firstSlot", equalTo(firstSlot)),
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("firstUpdatedBy", equalTo(firstUpdatedBy)),
                        HasPropertyWithValue.hasProperty("secondSlot", equalTo(secondSlot)),
                        HasPropertyWithValue.hasProperty(
                                "secondUpdatedBy", equalTo(secondUpdatedBy)),
                        HasPropertyWithValue.hasProperty("thirdSlot", equalTo(thirdSlot)),
                        HasPropertyWithValue.hasProperty("thirdUpdatedBy", equalTo(thirdUpdatedBy)),
                        HasPropertyWithValue.hasProperty("fourthSlot", equalTo(fourthSlot)),
                        HasPropertyWithValue.hasProperty(
                                "fourthUpdatedBy", equalTo(fourthUpdatedBy)),
                        HasPropertyWithValue.hasProperty("fifthSlot", equalTo(fifthSlot)),
                        HasPropertyWithValue.hasProperty("fifthUpdatedBy", equalTo(fifthUpdatedBy)),
                        HasPropertyWithValue.hasProperty("sixthSlot", equalTo(sixthSlot)),
                        HasPropertyWithValue.hasProperty("sixthUpdatedBy", equalTo(sixthUpdatedBy)),
                        HasPropertyWithValue.hasProperty("seventhSlot", equalTo(seventhSlot)),
                        HasPropertyWithValue.hasProperty(
                                "seventhUpdatedBy", equalTo(seventhUpdatedBy)),
                        HasPropertyWithValue.hasProperty("eighthSlot", equalTo(eighthSlot)),
                        HasPropertyWithValue.hasProperty(
                                "eighthUpdatedBy", equalTo(eighthUpdatedBy)),
                        HasPropertyWithValue.hasProperty("lastUpdatedAt", equalTo(context.now())),
                        HasPropertyWithValue.hasProperty(
                                "reportDate", equalTo(LocalDate.of(2021, 10, 14))))));
    }

    @SneakyThrows
    @Test
    void testGetSleepTrackerNote_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        SleepTrackerNotes sleepTrackerNotes =
                SleepTrackerNotes.builder()
                        .client(client)
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Awake at 6:00am")
                        .seventhSlot("")
                        .seventhUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(context.now())
                        .build();
        mockSleepTrackerNotesRepository.save(sleepTrackerNotes);

        when(mockSleepTrackerNotesRepository.findByClient_IdAndReportDate(
                        1L, LocalDate.parse("2021-10-18")))
                .thenReturn(sleepTrackerNotes);
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        SleepTrackerNotesDTO expected =
                new ModelMapper().map(sleepTrackerNotes, SleepTrackerNotesDTO.class);

        // When
        SleepTrackerNotesDTO actual =
                sleepTrackerNotesService.getSleepTrackerNote(1L, "2021-10-18");

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateSleepTrackerNotes_Success() {
        // Given
        long id = 1L;
        String firstSlot = "Awake at 12:00am";
        String firstUpdatedBy = "mary";
        String secondSlot = "Awake at 1:00am";
        String secondUpdatedBy = "zen";
        String thirdSlot = "";
        String thirdUpdatedBy = "";
        String fourthSlot = "Awake at 2:00am";
        String fourthUpdatedBy = "mary";
        String fifthSlot = "Awake at 2:00am";
        String fifthUpdatedBy = "mary";
        String sixthSlot = "";
        String sixthUpdatedBy = "";
        String seventhSlot = "Awake at 2:00am";
        String seventhUpdatedBy = "mary";
        String eighthSlot = "Awake at 2:00am";
        String eighthUpdatedBy = "mary";
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(id).name("name").build();
        mockClientRepository.save(client);

        SleepTrackerNotes sleepTrackerNotes =
                SleepTrackerNotes.builder()
                        .id(1L)
                        .client(client)
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Awake at 6:00am")
                        .seventhSlot("")
                        .seventhUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        UpdateSleepTrackerRequest request =
                UpdateSleepTrackerRequest.builder()
                        .id(id)
                        .firstSlot(firstSlot)
                        .firstUpdatedBy(firstUpdatedBy)
                        .secondSlot(secondSlot)
                        .secondUpdatedBy(secondUpdatedBy)
                        .thirdSlot(thirdSlot)
                        .thirdUpdatedBy(thirdUpdatedBy)
                        .fourthSlot(fourthSlot)
                        .fourthUpdatedBy(fourthUpdatedBy)
                        .fifthSlot(fifthSlot)
                        .fifthUpdatedBy(fifthUpdatedBy)
                        .sixthSlot(sixthSlot)
                        .sixthUpdatedBy(sixthUpdatedBy)
                        .seventhSlot(seventhSlot)
                        .seventhUpdatedBy(seventhUpdatedBy)
                        .eighthSlot(eighthSlot)
                        .eighthUpdatedBy(eighthUpdatedBy)
                        .currentLastUpdatedAt(
                                sleepTrackerNotes.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();
        when(mockSleepTrackerNotesRepository.findById(1L))
                .thenReturn(Optional.of(sleepTrackerNotes));

        // when
        sleepTrackerNotesService.updateSleepTracker(id, request);

        SleepTrackerNotes expected =
                SleepTrackerNotes.builder()
                        .id(id)
                        .client(sleepTrackerNotes.getClient())
                        .firstSlot(firstSlot)
                        .firstUpdatedBy(firstUpdatedBy)
                        .secondSlot(secondSlot)
                        .secondUpdatedBy(secondUpdatedBy)
                        .thirdSlot(thirdSlot)
                        .thirdUpdatedBy(thirdUpdatedBy)
                        .fourthSlot(fourthSlot)
                        .fourthUpdatedBy(fourthUpdatedBy)
                        .fifthSlot(fifthSlot)
                        .fifthUpdatedBy(fifthUpdatedBy)
                        .sixthSlot(sixthSlot)
                        .sixthUpdatedBy(sixthUpdatedBy)
                        .seventhSlot(seventhSlot)
                        .seventhUpdatedBy(seventhUpdatedBy)
                        .eighthSlot(eighthSlot)
                        .eighthUpdatedBy(eighthUpdatedBy)
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(sleepTrackerNotes.getLastUpdatedAt())
                        .build();
        // then
        verify(mockSleepTrackerNotesRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateSleepTrackerNotes_Failure() {
        // Given
        UpdateSleepTrackerRequest request =
                UpdateSleepTrackerRequest.builder()
                        .firstSlot("Awake at 1:00am")
                        .firstUpdatedBy("Helen")
                        .secondSlot("yu")
                        .secondUpdatedBy("joe")
                        .thirdSlot("ftg")
                        .thirdUpdatedBy("ross")
                        .fourthSlot("")
                        .fourthUpdatedBy("")
                        .sixthSlot("vg")
                        .sixthUpdatedBy("Awake at 6:00am")
                        .seventhSlot("")
                        .seventhUpdatedBy("")
                        .eighthSlot("vb")
                        .eighthUpdatedBy("jess")
                        .currentLastUpdatedAt(Instant.now().truncatedTo(ChronoUnit.MILLIS))
                        .build();
        assertThrows(
                SleepTrackerNotesServiceException.class,
                () -> {

                    // When
                    sleepTrackerNotesService.updateSleepTracker(1L, request);
                });
    }
}
