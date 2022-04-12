package com.onedoorway.project.services;

import static com.onedoorway.project.model.LookupType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.LookupDTO;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.repository.LookupRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LookupServiceTest {

    @Mock LookupRepository mockLookupRepository;

    private LookupService lookupService;

    @BeforeEach
    void init() {
        lookupService = new LookupService(mockLookupRepository);
    }

    @Test
    void testListAllHouseFactors_Success() {
        // Given
        Lookup lookup1 =
                Lookup.builder().id(1).name("one").lookupType(INCIDENT_HOUSE_ASSET_FACTOR).build();

        Lookup lookup2 =
                Lookup.builder().id(2).name("two").lookupType(INCIDENT_HOUSE_ASSET_FACTOR).build();

        when(mockLookupRepository.findAllByLookupType(INCIDENT_HOUSE_ASSET_FACTOR))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_HOUSE_ASSET_FACTOR);

        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllHouseFactors_Success_Empty() {

        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_HOUSE_ASSET_FACTOR);

        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllCategories_Success() {
        // Given
        Lookup lookup1 = Lookup.builder().id(1).name("one").lookupType(INCIDENT_CATEGORY).build();

        Lookup lookup2 = Lookup.builder().id(2).name("two").lookupType(INCIDENT_CATEGORY).build();

        when(mockLookupRepository.findAllByLookupType(INCIDENT_CATEGORY))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_CATEGORY);

        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllCategories_Success_Empty() {

        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_CATEGORY);

        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllTypes_Success() {
        // Given
        Lookup lookup1 = Lookup.builder().id(1).name("one").lookupType(INCIDENT_TYPE).build();

        Lookup lookup2 = Lookup.builder().id(2).name("two").lookupType(INCIDENT_TYPE).build();

        when(mockLookupRepository.findAllByLookupType(INCIDENT_TYPE))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_TYPE);

        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllTypes_Success_Empty() {

        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_TYPE);

        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllClassification_Success() {
        // Given
        Lookup lookup1 =
                Lookup.builder().id(1).name("one").lookupType(INCIDENT_CLASSIFICATION).build();
        Lookup lookup2 =
                Lookup.builder().id(2).name("two").lookupType(INCIDENT_CLASSIFICATION).build();
        when(mockLookupRepository.findAllByLookupType(INCIDENT_CLASSIFICATION))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_CLASSIFICATION);
        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllClassification_Success_Empty() {
        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_CLASSIFICATION);
        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllEnvironmentFactorSuccess() {
        // Given
        Lookup lookup1 =
                Lookup.builder().id(1).name("one").lookupType(INCIDENT_ENVIRONMENT_FACTOR).build();
        Lookup lookup2 =
                Lookup.builder().id(2).name("two").lookupType(INCIDENT_ENVIRONMENT_FACTOR).build();
        when(mockLookupRepository.findAllByLookupType(INCIDENT_ENVIRONMENT_FACTOR))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_ENVIRONMENT_FACTOR);
        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllEnvironmentFactor_Success_Empty() {
        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_ENVIRONMENT_FACTOR);
        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllPersonFactorSuccess() {
        // Given
        Lookup lookup1 =
                Lookup.builder().id(1).name("one").lookupType(INCIDENT_PERSON_FACTOR).build();
        Lookup lookup2 =
                Lookup.builder().id(2).name("two").lookupType(INCIDENT_PERSON_FACTOR).build();
        when(mockLookupRepository.findAllByLookupType(INCIDENT_PERSON_FACTOR))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_PERSON_FACTOR);
        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllPersonFactor_Success_Empty() {
        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(INCIDENT_PERSON_FACTOR);
        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllReportsSuccess() {
        // Given
        Lookup lookup1 = Lookup.builder().id(1).name("one").lookupType(REPORTS).build();
        Lookup lookup2 = Lookup.builder().id(2).name("two").lookupType(REPORTS).build();
        when(mockLookupRepository.findAllByLookupType(REPORTS))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(REPORTS);
        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllReports_Success_Empty() {
        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(REPORTS);
        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllNightlyTasksSuccess() {
        // Given
        Lookup lookup1 = Lookup.builder().id(1).name("one").lookupType(NIGHTLY_TASKS).build();
        Lookup lookup2 = Lookup.builder().id(2).name("two").lookupType(NIGHTLY_TASKS).build();
        when(mockLookupRepository.findAllByLookupType(NIGHTLY_TASKS))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(NIGHTLY_TASKS);
        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllNightlyTasks_Success_Empty() {
        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(NIGHTLY_TASKS);
        // Then
        assertEquals(lookupDTOS.size(), 0);
    }

    @Test
    void testListAllMiscellaneousNotesSuccess() {
        // Given
        Lookup lookup1 = Lookup.builder().id(1).name("one").lookupType(MISCELLANEOUS_NOTES).build();
        Lookup lookup2 = Lookup.builder().id(2).name("two").lookupType(MISCELLANEOUS_NOTES).build();
        when(mockLookupRepository.findAllByLookupType(MISCELLANEOUS_NOTES))
                .thenReturn(List.of(lookup1, lookup2));
        List<LookupDTO> lookupDTOS = lookupService.listLookups(MISCELLANEOUS_NOTES);
        // Then
        assertEquals(2, lookupDTOS.size());
        assertThat(
                lookupDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(lookup1.getName()),
                                                equalTo(lookup2.getName())))))));
    }

    @Test
    void testListAllMiscellaneousNotes_Success_Empty() {
        // When
        List<LookupDTO> lookupDTOS = lookupService.listLookups(MISCELLANEOUS_NOTES);
        // Then
        assertEquals(lookupDTOS.size(), 0);
    }
}
