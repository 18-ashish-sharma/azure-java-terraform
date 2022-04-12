package com.onedoorway.project.services;

import static com.onedoorway.project.model.LookupType.CASE_NOTES;
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
import com.onedoorway.project.exception.CaseNoteServiceException;
import com.onedoorway.project.model.CaseNote;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.repository.CaseNoteRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.LookupRepository;
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
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CaseNoteServiceTest {
    @Mock CaseNoteRepository mockCaseNoteRepository;
    @Mock ClientRepository mockClientRepository;
    @Mock LookupRepository mockLookupRepository;
    private CaseNoteService caseNoteService;
    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        caseNoteService =
                new CaseNoteService(
                        mockCaseNoteRepository,
                        mockClientRepository,
                        mockLookupRepository,
                        context);
    }

    @SneakyThrows
    @Test
    void testCreateCaseNote_Success() {
        // Given
        String content = "content";
        String subject = "subject";
        String noteDate = "2021-09-09";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        String lastUploadedBy = "sam";

        Lookup category = Lookup.builder().id(1L).build();
        CaseNoteRequest request =
                CaseNoteRequest.builder()
                        .clientId(1L)
                        .categoryId(category.getId())
                        .content(content)
                        .subject(subject)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(mockLookupRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // When
        caseNoteService.createCaseNote(request);

        // Then
        CaseNote expected =
                CaseNote.builder()
                        .id(1L)
                        .client(client)
                        .category(category)
                        .content(content)
                        .subject(subject)
                        .deleted(false)
                        .startTime(
                                LocalDateTime.parse(
                                        startTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .endTime(
                                LocalDateTime.parse(
                                        endTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .noteDate(LocalDate.parse(noteDate))
                        .lastUploadedBy(lastUploadedBy)
                        .lastUpdatedAt(context.now())
                        .build();

        ArgumentCaptor<CaseNote> caseNoteArgumentCaptor = ArgumentCaptor.forClass(CaseNote.class);
        verify(mockCaseNoteRepository).save(caseNoteArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("category", equalTo(category)),
                        HasPropertyWithValue.hasProperty("lastUpdatedAt", equalTo(context.now())),
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
                        HasPropertyWithValue.hasProperty("content", equalTo(content)),
                        HasPropertyWithValue.hasProperty("subject", equalTo(subject)),
                        HasPropertyWithValue.hasProperty(
                                "noteDate", equalTo(LocalDate.parse(noteDate))))));
    }

    @Test
    void testCreateCaseNote_Failure_ClientNotFound() {
        // Given
        String content = "content";
        String subject = "subject";
        String noteDate = "2021-09-09";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        String lastUploadedBy = "sam";

        CaseNoteRequest request =
                CaseNoteRequest.builder()
                        .clientId(1L)
                        .categoryId(2L)
                        .content(content)
                        .subject(subject)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                CaseNoteServiceException.class,
                () -> {
                    // When
                    caseNoteService.createCaseNote(request);
                });
    }

    @Test
    void testCreateCaseNote_Failure_LookupNotFound() {
        // Given
        String content = "content";
        String subject = "subject";
        String noteDate = "2021-09-09";
        String startTime = "2021-09-09 10:00:00";
        String endTime = "2021-09-09 12:00:00";
        String lastUploadedBy = "sam";

        Client client = Client.builder().id(1).name("client").build();

        CaseNoteRequest request =
                CaseNoteRequest.builder()
                        .clientId(client.getId())
                        .categoryId(2L)
                        .content(content)
                        .subject(subject)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                CaseNoteServiceException.class,
                () -> {
                    // When
                    caseNoteService.createCaseNote(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetCaseNote_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        lookup = mockLookupRepository.save(lookup);

        CaseNote caseNote =
                CaseNote.builder()
                        .id(id)
                        .client(client)
                        .category(lookup)
                        .content("content")
                        .subject("sub")
                        .deleted(false)
                        .noteDate(LocalDate.now())
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .lastUploadedBy("tina")
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockCaseNoteRepository.findById(id)).thenReturn(Optional.of(caseNote));
        CaseNoteDTO expected = new ModelMapper().map(caseNote, CaseNoteDTO.class);

        // When
        CaseNoteDTO actual = caseNoteService.getCaseNote(id);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetCaseNote_Failure_CaseNoteNotFound() {
        // Given
        long id = 1;
        assertThrows(
                CaseNoteServiceException.class,
                () -> {
                    // When
                    caseNoteService.getCaseNote(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListCaseNote_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        CaseNote caseNote =
                CaseNote.builder()
                        .id(1L)
                        .client(client)
                        .category(lookup)
                        .noteDate(LocalDate.now())
                        .subject("sub")
                        .content("content")
                        .deleted(false)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(context.now())
                        .build();
        when(mockCaseNoteRepository.findAllByCategory_NameAndClient_IdAndNoteDateBetween(
                        any(), any(), any(), any(), any()))
                .thenReturn(List.of(caseNote));
        ListCaseNoteRequest request =
                ListCaseNoteRequest.builder()
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .clientId(id)
                        .category(lookup.getName())
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        List<CaseNoteDTO> caseNoteDTOS = caseNoteService.listCaseNotes(request);

        // Then
        assertEquals(caseNoteDTOS.size(), 1);
        assertThat(
                caseNoteDTOS,
                (allOf(
                        everyItem(hasProperty("id", AnyOf.anyOf(equalTo(1L)))),
                        everyItem(
                                hasProperty(
                                        "category",
                                        AnyOf.anyOf(
                                                equalTo(
                                                        LookupDTO.builder()
                                                                .name("test")
                                                                .build())))),
                        everyItem(
                                hasProperty(
                                        "clientId",
                                        AnyOf.anyOf(equalTo(caseNote.getClient().getId())))),
                        everyItem(
                                hasProperty(
                                        "subject", AnyOf.anyOf(equalTo(caseNote.getSubject())))),
                        everyItem(
                                hasProperty(
                                        "content", AnyOf.anyOf(equalTo(caseNote.getContent())))),
                        everyItem(
                                hasProperty(
                                        "noteDate", AnyOf.anyOf(equalTo(caseNote.getNoteDate())))),
                        everyItem(
                                hasProperty(
                                        "startTime",
                                        AnyOf.anyOf(equalTo(caseNote.getStartTime())))),
                        everyItem(
                                hasProperty(
                                        "endTime", AnyOf.anyOf(equalTo(caseNote.getEndTime())))),
                        everyItem(
                                hasProperty(
                                        "lastUploadedBy",
                                        AnyOf.anyOf(equalTo(caseNote.getLastUploadedBy())))),
                        everyItem(
                                hasProperty(
                                        "lastUpdatedAt", AnyOf.anyOf(equalTo(context.now())))))));
    }

    @Test
    void testListAllCaseNotes_Success_Empty() {
        // Given
        Lookup lookup = Lookup.builder().name("test").lookupType(CASE_NOTES).build();
        long id = 1;
        ListCaseNoteRequest request =
                ListCaseNoteRequest.builder()
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .category(lookup.getName())
                        .clientId(id)
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        List<CaseNoteDTO> caseNoteDTOS = caseNoteService.listCaseNotes(request);

        // Then
        assertEquals(caseNoteDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateCaseNote_Success() {
        // Given
        long id = 1L;
        String subject = "subject 11";
        String content = "Content 11";
        String startTime = "2021-11-20 10:00:00";
        String endTime = "2021-11-21 11:00:00";
        String lastUploadedBy = "sina";
        String noteDate = "2021-11-12";
        Client client = Client.builder().id(1).name("client").build();
        client = mockClientRepository.save(client);
        Lookup category = Lookup.builder().id(19).name("test").lookupType(CASE_NOTES).build();
        mockLookupRepository.save(category);

        CaseNote caseNote =
                CaseNote.builder()
                        .client(client)
                        .category(category)
                        .content("content")
                        .subject("subject")
                        .deleted(false)
                        .startTime(LocalDateTime.now())
                        .endTime(LocalDateTime.now())
                        .lastUploadedBy("seema")
                        .noteDate(LocalDate.now())
                        .lastUpdatedAt(context.now())
                        .build();

        UpdateCaseNoteRequest request =
                UpdateCaseNoteRequest.builder()
                        .subject(subject)
                        .content(content)
                        .categoryId(1L)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .deleted(true)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        when(mockCaseNoteRepository.findById(1L)).thenReturn(Optional.of(caseNote));
        when(mockLookupRepository.findById(request.getCategoryId()))
                .thenReturn(Optional.of(category));
        // When
        caseNoteService.updateCaseNote(1L, request);

        CaseNote expected =
                CaseNote.builder()
                        .client(client)
                        .category(caseNote.getCategory())
                        .noteDate(caseNote.getNoteDate())
                        .subject(caseNote.getSubject())
                        .content(caseNote.getContent())
                        .deleted(true)
                        .startTime(
                                LocalDateTime.parse(
                                        startTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .endTime(
                                LocalDateTime.parse(
                                        endTime,
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .lastUploadedBy(caseNote.getLastUploadedBy())
                        .lastUpdatedAt(caseNote.getLastUpdatedAt())
                        .build();

        // Then
        verify(mockCaseNoteRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateCaseNote_Failure_CaseNoteNotFound() {
        // Given
        long id = 1L;
        String subject = "subject 11";
        String content = "Content 11";
        String startTime = "2021-11-20 10:00:00";
        String noteDate = "2021-11-12";
        String endTime = "2021-11-21 11:00:00";
        String lastUploadedBy = "sina";

        UpdateCaseNoteRequest request =
                UpdateCaseNoteRequest.builder()
                        .subject(subject)
                        .categoryId(2L)
                        .content(content)
                        .startTime(startTime)
                        .endTime(endTime)
                        .noteDate(noteDate)
                        .deleted(false)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                CaseNoteServiceException.class,
                () -> {
                    // When
                    caseNoteService.updateCaseNote(id, request);
                });
    }
}
