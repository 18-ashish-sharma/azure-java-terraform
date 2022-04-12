package com.onedoorway.project.services;

import static com.onedoorway.project.model.LookupType.MISCELLANEOUS_NOTES;
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
import com.onedoorway.project.dto.ListMiscellaneousNoteRequest;
import com.onedoorway.project.dto.MiscellaneousNoteDTO;
import com.onedoorway.project.dto.MiscellaneousNoteRequest;
import com.onedoorway.project.dto.UpdateMiscellaneousNoteRequest;
import com.onedoorway.project.exception.MiscellaneousNoteServiceException;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.model.MiscellaneousNote;
import com.onedoorway.project.repository.LookupRepository;
import com.onedoorway.project.repository.MiscellaneousNoteRepository;
import java.time.Instant;
import java.time.LocalDate;
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
class MiscellaneousNoteServiceTest {
    @Mock MiscellaneousNoteRepository mockMiscellaneousNoteRepository;
    @Mock LookupRepository mockLookupRepository;
    private MiscellaneousNoteService miscellaneousNoteService;
    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        miscellaneousNoteService =
                new MiscellaneousNoteService(
                        mockMiscellaneousNoteRepository, mockLookupRepository, context);
    }

    @SneakyThrows
    @Test
    void testCreateMiscellaneousNote_Success() {
        // Given
        String content = "content";
        String subject = "subject";
        String noteDate = "2021-12-03";
        String lastUploadedBy = "sam";
        String client = "client";
        String user = "user";
        String house = "house";

        MiscellaneousNoteRequest request =
                MiscellaneousNoteRequest.builder()
                        .categoryId(1L)
                        .content(content)
                        .subject(subject)
                        .noteDate(noteDate)
                        .client(client)
                        .house(house)
                        .user(user)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        Lookup category = Lookup.builder().id(1L).build();
        when(mockLookupRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // When
        miscellaneousNoteService.createMiscellaneousNote(request);

        // Then
        MiscellaneousNote expected =
                MiscellaneousNote.builder()
                        .id(1L)
                        .category(category)
                        .content(content)
                        .subject(subject)
                        .deleted(false)
                        .house(house)
                        .user(user)
                        .client(client)
                        .noteDate(LocalDate.parse(noteDate))
                        .lastUploadedBy(lastUploadedBy)
                        .lastUpdatedAt(context.now())
                        .build();

        ArgumentCaptor<MiscellaneousNote> miscellaneousNoteArgumentCaptor =
                ArgumentCaptor.forClass(MiscellaneousNote.class);
        verify(mockMiscellaneousNoteRepository).save(miscellaneousNoteArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("category", equalTo(category)),
                        HasPropertyWithValue.hasProperty("lastUpdatedAt", equalTo(context.now())),
                        HasPropertyWithValue.hasProperty("content", equalTo(content)),
                        HasPropertyWithValue.hasProperty("subject", equalTo(subject)),
                        HasPropertyWithValue.hasProperty(
                                "noteDate", equalTo(LocalDate.parse(noteDate))),
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("house", equalTo(house)),
                        HasPropertyWithValue.hasProperty("user", equalTo(user)))));
    }

    @Test
    void testCreateMiscellaneousNote_Failure_CategoryNotFound() {
        // Given
        String content = "content";
        String subject = "subject";
        String noteDate = "2021-09-09";
        String lastUploadedBy = "sam";
        String user = "user";
        String client = "client";
        String house = "house";

        MiscellaneousNoteRequest request =
                MiscellaneousNoteRequest.builder()
                        .categoryId(1L)
                        .content(content)
                        .subject(subject)
                        .noteDate(noteDate)
                        .user(user)
                        .client(client)
                        .house(house)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                MiscellaneousNoteServiceException.class,
                () -> {
                    // When
                    miscellaneousNoteService.createMiscellaneousNote(request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateMiscellaneousNote_Success() {
        // Given
        long id = 1L;
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
                        .lastUpdatedAt(context.now())
                        .build();

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

        when(mockMiscellaneousNoteRepository.findById(1L))
                .thenReturn(Optional.of(miscellaneousNote));

        // When
        miscellaneousNoteService.updateMiscellaneousNote(id, request);

        MiscellaneousNote expected =
                MiscellaneousNote.builder()
                        .subject(subject)
                        .content(content)
                        .client(miscellaneousNote.getClient())
                        .house(miscellaneousNote.getHouse())
                        .user(miscellaneousNote.getUser())
                        .deleted(true)
                        .lastUploadedBy(lastUploadedBy)
                        .lastUpdatedAt(miscellaneousNote.getLastUpdatedAt())
                        .build();

        // Then
        verify(mockMiscellaneousNoteRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateMiscellaneousNote_Failure_MiscellaneousNoteNotFound() {
        // Given
        long id = 1L;
        String subject = "subject 11";
        String content = "Content 11";
        String house = "house";
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
                        .deleted(false)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                MiscellaneousNoteServiceException.class,
                () -> {
                    // When
                    miscellaneousNoteService.updateMiscellaneousNote(id, request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListMiscellaneousNote_Success() {
        // Given
        Lookup lookup = Lookup.builder().name("test").lookupType(MISCELLANEOUS_NOTES).build();
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
        when(mockMiscellaneousNoteRepository.findAllByCategory_NameAndNoteDateBetween(
                        any(), any(), any(), any()))
                .thenReturn(List.of(miscellaneousNote));
        ListMiscellaneousNoteRequest request =
                ListMiscellaneousNoteRequest.builder()
                        .category(lookup.getName())
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        List<MiscellaneousNoteDTO> miscellaneousNoteDTOS =
                miscellaneousNoteService.listMiscellaneousNotes(request);

        // Then
        assertEquals(miscellaneousNoteDTOS.size(), 1);
        assertThat(
                miscellaneousNoteDTOS,
                (allOf(
                        everyItem(
                                hasProperty("id", AnyOf.anyOf(equalTo(miscellaneousNote.getId())))),
                        everyItem(
                                hasProperty(
                                        "subject",
                                        AnyOf.anyOf(equalTo(miscellaneousNote.getSubject())))),
                        everyItem(
                                hasProperty(
                                        "content",
                                        AnyOf.anyOf(equalTo(miscellaneousNote.getContent())))),
                        everyItem(
                                hasProperty(
                                        "user", AnyOf.anyOf(equalTo(miscellaneousNote.getUser())))),
                        everyItem(
                                hasProperty(
                                        "house",
                                        AnyOf.anyOf(equalTo(miscellaneousNote.getHouse())))),
                        everyItem(
                                hasProperty(
                                        "client",
                                        AnyOf.anyOf(equalTo(miscellaneousNote.getClient())))),
                        everyItem(
                                hasProperty(
                                        "noteDate",
                                        AnyOf.anyOf(equalTo(miscellaneousNote.getNoteDate())))),
                        everyItem(
                                hasProperty(
                                        "lastUploadedBy",
                                        AnyOf.anyOf(
                                                equalTo(miscellaneousNote.getLastUploadedBy())))),
                        everyItem(
                                hasProperty(
                                        "lastUpdatedAt",
                                        AnyOf.anyOf(
                                                equalTo(miscellaneousNote.getLastUpdatedAt())))))));
    }

    @Test
    void testListAllMiscellaneousNotes_Success_Empty() {
        // Given
        Lookup lookup = Lookup.builder().name("test").lookupType(MISCELLANEOUS_NOTES).build();
        ListMiscellaneousNoteRequest request =
                ListMiscellaneousNoteRequest.builder()
                        .start("2021-11-18")
                        .end("2021-11-19")
                        .category(lookup.getName())
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        List<MiscellaneousNoteDTO> miscellaneousNoteDTOS =
                miscellaneousNoteService.listMiscellaneousNotes(request);

        // Then
        assertEquals(miscellaneousNoteDTOS.size(), 0);
    }
}
