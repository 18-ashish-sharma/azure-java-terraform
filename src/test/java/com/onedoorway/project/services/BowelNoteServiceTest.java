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
import com.onedoorway.project.dto.BowelNoteDTO;
import com.onedoorway.project.dto.BowelNoteRequest;
import com.onedoorway.project.dto.ListBowelNoteRequest;
import com.onedoorway.project.dto.UpdateBowelNoteRequest;
import com.onedoorway.project.exception.BowelNoteServiceException;
import com.onedoorway.project.model.BowelNote;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.Size;
import com.onedoorway.project.repository.BowelNoteRepository;
import com.onedoorway.project.repository.ClientRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
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
class BowelNoteServiceTest {
    @Mock BowelNoteRepository mockBowelNoteRepository;
    @Mock ClientRepository mockClientRepository;
    private BowelNoteService bowelNoteService;
    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        bowelNoteService =
                new BowelNoteService(mockBowelNoteRepository, mockClientRepository, context);
    }

    @SneakyThrows
    @Test
    void testCreateBowelNote_Success() {
        // Given
        String size = "Small";
        Boolean type1 = false;
        Boolean type2 = false;
        Boolean type3 = false;
        Boolean type4 = false;
        Boolean type5 = false;
        Boolean type6 = false;
        Boolean type7 = false;
        String startDate = "2021-09-09";
        String recordTime = "2021-09-09 10:00:00";
        String lastUploadedBy = "sam";

        BowelNoteRequest request =
                BowelNoteRequest.builder()
                        .clientId(1L)
                        .size(size)
                        .type1(type1)
                        .type2(type2)
                        .type3(type3)
                        .type4(type4)
                        .type5(type5)
                        .type6(type6)
                        .type7(type7)
                        .startDate(startDate)
                        .recordTime(recordTime)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        bowelNoteService.createBowelNote(request);

        // Then
        BowelNote expected =
                BowelNote.builder()
                        .id(1L)
                        .client(client)
                        .size(Size.valueOf(size))
                        .type1(type1)
                        .type2(type2)
                        .type3(type3)
                        .type4(type4)
                        .type5(type5)
                        .type6(type6)
                        .type7(type7)
                        .deleted(false)
                        .recordTime(
                                LocalDateTime.parse(
                                        recordTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .startDate(
                                LocalDate.parse(
                                        startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .lastUploadedBy(lastUploadedBy)
                        .lastUpdatedAt(context.now())
                        .build();

        ArgumentCaptor<BowelNote> bowelNoteArgumentCaptor =
                ArgumentCaptor.forClass(BowelNote.class);
        verify(mockBowelNoteRepository).save(bowelNoteArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("lastUpdatedAt", equalTo(context.now())),
                        HasPropertyWithValue.hasProperty(
                                "recordTime",
                                equalTo(
                                        LocalDateTime.parse(
                                                recordTime,
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss")))),
                        HasPropertyWithValue.hasProperty(
                                "startDate",
                                equalTo(
                                        LocalDate.parse(
                                                startDate,
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd")))),
                        HasPropertyWithValue.hasProperty("type1", equalTo(type1)),
                        HasPropertyWithValue.hasProperty("type2", equalTo(type2)),
                        HasPropertyWithValue.hasProperty("type3", equalTo(type3)),
                        HasPropertyWithValue.hasProperty("type4", equalTo(type4)),
                        HasPropertyWithValue.hasProperty("type5", equalTo(type5)),
                        HasPropertyWithValue.hasProperty("type6", equalTo(type6)),
                        HasPropertyWithValue.hasProperty("type7", equalTo(type7)),
                        HasPropertyWithValue.hasProperty(
                                "lastUploadedBy", equalTo(lastUploadedBy)))));
    }

    @Test
    void testCreateBowelNote_Failure_NoClientFound() {
        // Given
        String size = "Small";
        Boolean type1 = false;
        Boolean type2 = false;
        Boolean type3 = false;
        Boolean type4 = false;
        Boolean type5 = false;
        Boolean type6 = false;
        Boolean type7 = false;
        String startDate = "2021-09-09";
        String recordTime = "2021-09-09 10:00:00";
        String lastUploadedBy = "sam";

        BowelNoteRequest request =
                BowelNoteRequest.builder()
                        .clientId(1L)
                        .size(size)
                        .type1(type1)
                        .type2(type2)
                        .type3(type3)
                        .type4(type4)
                        .type5(type5)
                        .type6(type6)
                        .type7(type7)
                        .startDate(startDate)
                        .recordTime(recordTime)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                BowelNoteServiceException.class,
                () -> {

                    // When
                    bowelNoteService.createBowelNote(request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListBowelNote_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        BowelNote bowelNote =
                BowelNote.builder()
                        .id(1L)
                        .client(client)
                        .startDate(LocalDate.now())
                        .recordTime(LocalDateTime.now())
                        .deleted(false)
                        .size(Size.Small)
                        .type1(false)
                        .type2(false)
                        .type3(false)
                        .type4(false)
                        .type5(false)
                        .type6(false)
                        .type7(false)
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(context.now())
                        .build();
        when(mockBowelNoteRepository.findAllByClient_IdAndStartDateBetween(
                        any(), any(), any(), any()))
                .thenReturn(List.of(bowelNote));
        ListBowelNoteRequest request =
                ListBowelNoteRequest.builder()
                        .start("2021-12-09")
                        .end("2021-12-10")
                        .clientId(id)
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        List<BowelNoteDTO> bowelNoteDTOS = bowelNoteService.listBowelNotes(request);

        // Then
        assertEquals(bowelNoteDTOS.size(), 1);
        assertThat(
                bowelNoteDTOS,
                (allOf(
                        everyItem(hasProperty("id", AnyOf.anyOf(equalTo(1L)))),
                        everyItem(
                                hasProperty(
                                        "clientId",
                                        AnyOf.anyOf(equalTo(bowelNote.getClient().getId())))),
                        everyItem(
                                hasProperty(
                                        "startDate",
                                        AnyOf.anyOf(equalTo(bowelNote.getStartDate())))),
                        everyItem(
                                hasProperty(
                                        "recordTime",
                                        AnyOf.anyOf(equalTo(bowelNote.getRecordTime())))),
                        everyItem(hasProperty("size", AnyOf.anyOf(equalTo(bowelNote.getSize())))),
                        everyItem(hasProperty("type1", AnyOf.anyOf(equalTo(bowelNote.getType1())))),
                        everyItem(hasProperty("type2", AnyOf.anyOf(equalTo(bowelNote.getType2())))),
                        everyItem(hasProperty("type3", AnyOf.anyOf(equalTo(bowelNote.getType3())))),
                        everyItem(hasProperty("type4", AnyOf.anyOf(equalTo(bowelNote.getType4())))),
                        everyItem(hasProperty("type5", AnyOf.anyOf(equalTo(bowelNote.getType5())))),
                        everyItem(hasProperty("type6", AnyOf.anyOf(equalTo(bowelNote.getType6())))),
                        everyItem(hasProperty("type7", AnyOf.anyOf(equalTo(bowelNote.getType7())))),
                        everyItem(
                                hasProperty(
                                        "lastUploadedBy",
                                        AnyOf.anyOf(equalTo(bowelNote.getLastUploadedBy())))),
                        everyItem(
                                hasProperty(
                                        "lastUpdatedAt", AnyOf.anyOf(equalTo(context.now())))))));
    }

    @Test
    void testListAllBowelNotes_Success_Empty() throws BowelNoteServiceException {
        // Given
        long id = 1;
        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        ListBowelNoteRequest request =
                ListBowelNoteRequest.builder()
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .clientId(id)
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        List<BowelNoteDTO> bowelNoteDTOS = bowelNoteService.listBowelNotes(request);

        // Then
        assertEquals(bowelNoteDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateBowelNote_Success() {
        // Given
        String recordTime = "2022-11-25 16:00:00";
        String size = "Small";
        String lastUploadedBy = "sam";
        String startDate = "2022-11-25";
        Long id = 1L;

        Client client = Client.builder().id(1).name("client").build();
        client = mockClientRepository.save(client);

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

        UpdateBowelNoteRequest updateBowelNoteRequest =
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

        when(mockBowelNoteRepository.findById(1L)).thenReturn(Optional.of(bowelNote));

        // When
        bowelNoteService.updateBowelNote(id, updateBowelNoteRequest);

        BowelNote expected =
                BowelNote.builder()
                        .id(id)
                        .client(client)
                        .size(Size.valueOf(size))
                        .recordTime(
                                LocalDateTime.parse(
                                        recordTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
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
                        .lastUpdatedAt(bowelNote.getLastUpdatedAt())
                        .build();
        // Then
        verify(mockBowelNoteRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateBowelNote_Failure_BowelNoteNotFound() {
        // Given
        String recordTime = "2022-11-25 16:00:00";
        String size = "Small";
        String lastUploadedBy = "sam";

        UpdateBowelNoteRequest updateBowelNoteRequest =
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

        assertThrows(
                BowelNoteServiceException.class,
                () -> {
                    // When
                    bowelNoteService.updateBowelNote(1L, updateBowelNoteRequest);
                });
    }
}
