package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.IncidentReviewDTO;
import com.onedoorway.project.exception.ReviewServiceException;
import com.onedoorway.project.model.Incident;
import com.onedoorway.project.model.IncidentReview;
import com.onedoorway.project.model.YesNo;
import com.onedoorway.project.repository.IncidentRepository;
import com.onedoorway.project.repository.IncidentReviewRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class IncidentReviewServiceTest {
    @Mock IncidentReviewRepository mockIncidentReviewRepository;

    @Mock IncidentRepository mockIncidentRepository;

    private IncidentReviewService incidentReviewService;

    @BeforeEach
    void init() {
        incidentReviewService =
                new IncidentReviewService(mockIncidentReviewRepository, mockIncidentRepository);
    }

    @SneakyThrows
    @Test
    void testCreateIncidentReview_Success() {
        // Given
        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .incidentId(1L)
                        .factorHouseDesign(true)
                        .factorHouseEquipment(true)
                        .factorHouseFumes(true)
                        .factorHouseGuards(true)
                        .factorHouseOther(true)
                        .factorHouseUnsuitable(true)
                        .factorHouseDescription("Room 12")
                        .factorHouseWhatHappened("Nothing")
                        .factorPersonName("Tom")
                        .factorPersonAlcohol(true)
                        .factorPersonComplacency(true)
                        .factorPersonFrustration(true)
                        .factorPersonLineOfFire(true)
                        .factorPersonLackTraining(true)
                        .factorPersonLossBalance(true)
                        .factorPersonManualHandling(true)
                        .factorPersonMedicalCondition(true)
                        .factorPersonNoPPE(true)
                        .factorPersonOther(true)
                        .factorPersonRushing(true)
                        .factorPersonThreatening(true)
                        .factorPersonFatigue(true)
                        .factorPersonUnsafePractice(true)
                        .factorPersonWorkingAlone(true)
                        .factorPersonDescription("Normal")
                        .factorPersonWhatHappened("Injured")
                        .factorEnvAnimals(true)
                        .factorEnvConfine(true)
                        .factorEnvNight(true)
                        .factorEnvOdours(true)
                        .factorEnvOther(true)
                        .factorEnvObstructions(true)
                        .factorEnvRaining(true)
                        .factorEnvSunGlare(true)
                        .factorEnvSurface(true)
                        .factorEnvTemperature(true)
                        .factorEnvVegetation(true)
                        .factorEnvHeight(true)
                        .factorEnvDescription("Description")
                        .factorEnvWhatHappened("Injured")
                        .factorWorkWhatHappened("Injured")
                        .riskRating(1)
                        .consequences("consequences")
                        .likelihood("risk")
                        .correctiveAction("correctiveAction")
                        .dueDate("1980-09-07")
                        .actionsToImplement("Immediate")
                        .allocatedTo("allocatedTo")
                        .factorWorkDescription("work")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes.name())
                        .furtherSupport(YesNo.Yes.name())
                        .supportPlanUpdate(YesNo.Yes.name())
                        .build();

        Incident incident = Incident.builder().id(1L).build();
        when(mockIncidentRepository.getById(1L)).thenReturn(incident);

        // When
        incidentReviewService.createIncidentReview(request);

        // Then
        IncidentReview expected =
                IncidentReview.builder()
                        .incident(incident)
                        .factorHouseDesign(true)
                        .factorHouseEquipment(true)
                        .factorHouseFumes(true)
                        .factorHouseGuards(true)
                        .factorHouseOther(true)
                        .factorHouseUnsuitable(true)
                        .factorHouseDescription("Room 12")
                        .factorHouseWhatHappened("Nothing")
                        .factorPersonName("Tom")
                        .factorPersonAlcohol(true)
                        .factorPersonComplacency(true)
                        .factorPersonFrustration(true)
                        .factorPersonLineOfFire(true)
                        .factorPersonLackTraining(true)
                        .factorPersonLossBalance(true)
                        .factorPersonManualHandling(true)
                        .factorPersonMedicalCondition(true)
                        .factorPersonNoPPE(true)
                        .factorPersonOther(true)
                        .factorPersonRushing(true)
                        .factorPersonThreatening(true)
                        .factorPersonFatigue(true)
                        .factorPersonUnsafePractice(true)
                        .factorPersonWorkingAlone(true)
                        .factorPersonDescription("Normal")
                        .factorPersonWhatHappened("Injured")
                        .factorEnvAnimals(true)
                        .factorEnvConfine(true)
                        .factorEnvNight(true)
                        .factorEnvOdours(true)
                        .factorEnvOther(true)
                        .factorEnvObstructions(true)
                        .factorEnvRaining(true)
                        .factorEnvSunGlare(true)
                        .factorEnvSurface(true)
                        .factorEnvTemperature(true)
                        .factorEnvVegetation(true)
                        .factorEnvHeight(true)
                        .factorEnvDescription("Description")
                        .factorEnvWhatHappened("Injured")
                        .factorWorkWhatHappened("Injured")
                        .riskRating(1)
                        .consequences("consequences")
                        .likelihood("risk")
                        .correctiveAction("correctiveAction")
                        .dueDate(LocalDate.of(1980, 9, 7))
                        .actionsToImplement("Immediate")
                        .allocatedTo("allocatedTo")
                        .factorWorkDescription("work")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes)
                        .furtherSupport(YesNo.Yes)
                        .supportPlanUpdate(YesNo.Yes)
                        .build();

        ArgumentCaptor<IncidentReview> incidentArgumentCaptor =
                ArgumentCaptor.forClass(IncidentReview.class);
        verify(mockIncidentReviewRepository).save(incidentArgumentCaptor.capture());

        IncidentReview actual = incidentArgumentCaptor.getValue();

        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetReview_Success() {
        // Given
        Incident incident = Incident.builder().id(1L).build();

        when(mockIncidentReviewRepository.findByIncident_Id(incident.getId()))
                .thenReturn(
                        List.of(
                                IncidentReview.builder()
                                        .id(1L)
                                        .incident(incident)
                                        .factorHouseDesign(true)
                                        .factorHouseEquipment(true)
                                        .factorHouseFumes(true)
                                        .factorHouseGuards(true)
                                        .factorHouseOther(true)
                                        .factorHouseUnsuitable(true)
                                        .factorHouseDescription("Room 12")
                                        .factorHouseWhatHappened("Nothing")
                                        .factorPersonName("Tom")
                                        .factorPersonAlcohol(true)
                                        .factorPersonComplacency(true)
                                        .factorPersonFrustration(true)
                                        .factorPersonLineOfFire(true)
                                        .factorPersonLackTraining(true)
                                        .factorPersonLossBalance(true)
                                        .factorPersonManualHandling(true)
                                        .factorPersonMedicalCondition(true)
                                        .factorPersonNoPPE(true)
                                        .factorPersonOther(true)
                                        .factorPersonRushing(true)
                                        .factorPersonThreatening(true)
                                        .factorPersonFatigue(true)
                                        .factorPersonUnsafePractice(true)
                                        .factorPersonWorkingAlone(true)
                                        .factorPersonDescription("Normal")
                                        .factorPersonWhatHappened("Injured")
                                        .factorEnvAnimals(true)
                                        .factorEnvConfine(true)
                                        .factorEnvNight(true)
                                        .factorEnvOdours(true)
                                        .factorEnvOther(true)
                                        .factorEnvObstructions(true)
                                        .factorEnvRaining(true)
                                        .factorEnvSunGlare(true)
                                        .factorEnvSurface(true)
                                        .factorEnvTemperature(true)
                                        .factorEnvVegetation(true)
                                        .factorEnvHeight(true)
                                        .factorEnvDescription("Description")
                                        .factorEnvWhatHappened("Injured")
                                        .factorWorkWhatHappened("Injured")
                                        .riskRating(1)
                                        .consequences("consequences")
                                        .likelihood("risk")
                                        .correctiveAction("correctiveAction")
                                        .dueDate(LocalDate.of(1980, 9, 7))
                                        .actionsToImplement("Immediate")
                                        .allocatedTo("allocatedTo")
                                        .factorWorkDescription("work")
                                        .howPrevented("howPrevented")
                                        .followUp("followUp")
                                        .codeBreach(YesNo.Yes)
                                        .furtherSupport(YesNo.Yes)
                                        .supportPlanUpdate(YesNo.Yes)
                                        .build()));

        // When
        List<IncidentReviewDTO> expected = incidentReviewService.getReviewById(1L);

        // Then
        assertEquals(expected.size(), 1);
        assertThat(
                expected,
                (allOf(
                        everyItem(HasPropertyWithValue.hasProperty("incidentId", equalTo(1L))),
                        everyItem(HasPropertyWithValue.hasProperty("id", equalTo(1L))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseDesign", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseEquipment", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseDesign", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseGuards", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseOther", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseUnsuitable", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseDescription", equalTo("Room 12"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorHouseWhatHappened", equalTo("Nothing"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonName", equalTo("Tom"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonAlcohol", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonComplacency", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonFrustration", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonLineOfFire", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonLackTraining", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonLossBalance", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonManualHandling", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonMedicalCondition", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonNoPPE", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonOther", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonRushing", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonThreatening", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonFatigue", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonUnsafePractice", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonWorkingAlone", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonDescription", equalTo("Normal"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorPersonWhatHappened", equalTo("Injured"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvAnimals", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvConfine", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("factorEnvNight", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("factorEnvOdours", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("factorEnvOther", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvObstructions", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvRaining", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvSunGlare", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvSurface", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvTemperature", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvVegetation", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("factorEnvHeight", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvDescription", equalTo("Description"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorEnvWhatHappened", equalTo("Injured"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorWorkWhatHappened", equalTo("Injured"))),
                        everyItem(HasPropertyWithValue.hasProperty("riskRating", equalTo(1))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "consequences", equalTo("consequences"))),
                        everyItem(HasPropertyWithValue.hasProperty("likelihood", equalTo("risk"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "correctiveAction", equalTo("correctiveAction"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("dueDate", equalTo("1980-09-07"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "actionsToImplement", equalTo("Immediate"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "allocatedTo", equalTo("allocatedTo"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "howPrevented", equalTo("howPrevented"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("followUp", equalTo("followUp"))),
                        everyItem(HasPropertyWithValue.hasProperty("codeBreach", equalTo("Yes"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "supportPlanUpdate", equalTo("Yes"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty("furtherSupport", equalTo("Yes"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "factorWorkDescription", equalTo("work"))))));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateReview_Success() {
        // Given
        long id = 1;
        Incident incident = Incident.builder().id(1L).build();
        IncidentReview review =
                IncidentReview.builder()
                        .id(1)
                        .incident(incident)
                        .factorHouseDesign(true)
                        .factorHouseEquipment(true)
                        .factorHouseFumes(true)
                        .factorHouseGuards(true)
                        .factorHouseOther(true)
                        .factorHouseUnsuitable(true)
                        .factorHouseDescription("Room 12")
                        .factorHouseWhatHappened("Nothing")
                        .factorPersonName("Tom")
                        .factorPersonAlcohol(true)
                        .factorPersonComplacency(true)
                        .factorPersonFrustration(true)
                        .factorPersonLineOfFire(true)
                        .factorPersonLackTraining(true)
                        .factorPersonLossBalance(true)
                        .factorPersonManualHandling(true)
                        .factorPersonMedicalCondition(true)
                        .factorPersonNoPPE(true)
                        .factorPersonOther(true)
                        .factorPersonRushing(true)
                        .factorPersonThreatening(true)
                        .factorPersonFatigue(true)
                        .factorPersonUnsafePractice(true)
                        .factorPersonWorkingAlone(true)
                        .factorPersonDescription("Normal")
                        .factorPersonWhatHappened("Injured")
                        .factorEnvAnimals(true)
                        .factorEnvConfine(true)
                        .factorEnvNight(true)
                        .factorEnvOdours(true)
                        .factorEnvOther(true)
                        .factorEnvObstructions(true)
                        .factorEnvRaining(true)
                        .factorEnvSunGlare(true)
                        .factorEnvSurface(true)
                        .factorEnvTemperature(true)
                        .factorEnvVegetation(true)
                        .factorEnvHeight(true)
                        .factorEnvDescription("Description")
                        .factorEnvWhatHappened("Injured")
                        .factorWorkWhatHappened("Injured")
                        .riskRating(1)
                        .consequences("consequences")
                        .likelihood("risk")
                        .correctiveAction("correctiveAction")
                        .dueDate(LocalDate.of(1980, 9, 7))
                        .actionsToImplement("Immediate")
                        .allocatedTo("allocatedTo")
                        .factorWorkDescription("work")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes)
                        .furtherSupport(YesNo.Yes)
                        .supportPlanUpdate(YesNo.Yes)
                        .build();

        when(mockIncidentReviewRepository.findById(id)).thenReturn(Optional.of(review));

        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .id(1L)
                        .factorHouseDesign(false)
                        .factorHouseEquipment(false)
                        .factorHouseFumes(false)
                        .factorHouseGuards(false)
                        .factorHouseOther(false)
                        .factorHouseUnsuitable(false)
                        .factorHouseDescription("Room 00")
                        .factorHouseWhatHappened("something")
                        .factorPersonName("sam")
                        .factorPersonAlcohol(false)
                        .factorPersonComplacency(false)
                        .factorPersonFrustration(false)
                        .factorPersonLineOfFire(false)
                        .factorPersonLackTraining(false)
                        .factorPersonLossBalance(false)
                        .factorPersonManualHandling(false)
                        .factorPersonMedicalCondition(false)
                        .factorPersonNoPPE(false)
                        .factorPersonOther(false)
                        .factorPersonRushing(false)
                        .factorPersonThreatening(false)
                        .factorPersonFatigue(false)
                        .factorPersonUnsafePractice(false)
                        .factorPersonWorkingAlone(false)
                        .factorPersonDescription("not Normal")
                        .factorPersonWhatHappened("not Injured")
                        .factorEnvAnimals(false)
                        .factorEnvConfine(false)
                        .factorEnvNight(false)
                        .factorEnvOdours(false)
                        .factorEnvOther(false)
                        .factorEnvObstructions(false)
                        .factorEnvRaining(false)
                        .factorEnvSunGlare(false)
                        .factorEnvSurface(false)
                        .factorEnvTemperature(false)
                        .factorEnvVegetation(false)
                        .factorEnvHeight(false)
                        .factorEnvDescription(" no Description")
                        .factorEnvWhatHappened("not Injured")
                        .factorWorkWhatHappened("not Injured")
                        .riskRating(1)
                        .consequences("no consequences")
                        .likelihood("no risk")
                        .correctiveAction("no correctiveAction")
                        .dueDate("1500-09-07")
                        .actionsToImplement("not Immediate")
                        .allocatedTo("not allocatedTo")
                        .factorWorkDescription("no work")
                        .reviewedBy("ami")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach("No")
                        .furtherSupport("No")
                        .supportPlanUpdate("No")
                        .build();
        // when
        incidentReviewService.updateReview(1L, request);

        IncidentReview expected =
                IncidentReview.builder()
                        .id(review.getId())
                        .incident(incident)
                        .factorHouseDesign(false)
                        .factorHouseEquipment(false)
                        .factorHouseFumes(false)
                        .factorHouseGuards(false)
                        .factorHouseOther(false)
                        .factorHouseUnsuitable(false)
                        .factorHouseDescription("Room 00")
                        .factorHouseWhatHappened("something")
                        .factorPersonName("sam")
                        .factorPersonAlcohol(false)
                        .factorPersonComplacency(false)
                        .factorPersonFrustration(false)
                        .factorPersonLineOfFire(false)
                        .factorPersonLackTraining(false)
                        .factorPersonLossBalance(false)
                        .factorPersonManualHandling(false)
                        .factorPersonMedicalCondition(false)
                        .factorPersonNoPPE(false)
                        .factorPersonOther(false)
                        .factorPersonRushing(false)
                        .factorPersonThreatening(false)
                        .factorPersonFatigue(false)
                        .factorPersonUnsafePractice(false)
                        .factorPersonWorkingAlone(false)
                        .factorPersonDescription("not Normal")
                        .factorPersonWhatHappened("not Injured")
                        .factorEnvAnimals(false)
                        .factorEnvConfine(false)
                        .factorEnvNight(false)
                        .factorEnvOdours(false)
                        .factorEnvOther(false)
                        .factorEnvObstructions(false)
                        .factorEnvRaining(false)
                        .factorEnvSunGlare(false)
                        .factorEnvSurface(false)
                        .factorEnvTemperature(false)
                        .factorEnvVegetation(false)
                        .factorEnvHeight(false)
                        .factorEnvDescription(" no Description")
                        .factorEnvWhatHappened("not Injured")
                        .factorWorkWhatHappened("not Injured")
                        .riskRating(1)
                        .consequences("no consequences")
                        .likelihood("no risk")
                        .correctiveAction("no correctiveAction")
                        .dueDate(LocalDate.of(1500, 9, 7))
                        .actionsToImplement("not Immediate")
                        .allocatedTo("not allocatedTo")
                        .factorWorkDescription("no work")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.No)
                        .furtherSupport(YesNo.No)
                        .supportPlanUpdate(YesNo.No)
                        .build();
        // Then
        ArgumentCaptor<IncidentReview> incidentArgumentCaptor =
                ArgumentCaptor.forClass(IncidentReview.class);
        verify(mockIncidentReviewRepository).save(incidentArgumentCaptor.capture());

        IncidentReview actual = incidentArgumentCaptor.getValue();

        assertEquals(expected, actual);
    }

    @Test
    void testUpdateReview_Failure_ReviewNotFound() {
        // Given
        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .id(100L)
                        .factorHouseDesign(false)
                        .factorHouseEquipment(false)
                        .factorHouseFumes(false)
                        .factorHouseGuards(false)
                        .factorHouseOther(false)
                        .factorHouseUnsuitable(false)
                        .factorHouseDescription("Room 00")
                        .factorHouseWhatHappened("something")
                        .factorPersonName("sam")
                        .factorPersonAlcohol(false)
                        .factorPersonComplacency(false)
                        .factorPersonFrustration(false)
                        .factorPersonLineOfFire(false)
                        .factorPersonLackTraining(false)
                        .factorPersonLossBalance(false)
                        .factorPersonManualHandling(false)
                        .factorPersonMedicalCondition(false)
                        .factorPersonNoPPE(false)
                        .factorPersonOther(false)
                        .factorPersonRushing(false)
                        .factorPersonThreatening(false)
                        .factorPersonFatigue(false)
                        .factorPersonUnsafePractice(false)
                        .factorPersonWorkingAlone(false)
                        .factorPersonDescription("not Normal")
                        .factorPersonWhatHappened("not Injured")
                        .factorEnvAnimals(false)
                        .factorEnvConfine(false)
                        .factorEnvNight(false)
                        .factorEnvOdours(false)
                        .factorEnvOther(false)
                        .factorEnvObstructions(false)
                        .factorEnvRaining(false)
                        .factorEnvSunGlare(false)
                        .factorEnvSurface(false)
                        .factorEnvTemperature(false)
                        .factorEnvVegetation(false)
                        .factorEnvHeight(false)
                        .factorEnvDescription(" no Description")
                        .factorEnvWhatHappened("not Injured")
                        .factorWorkWhatHappened("not Injured")
                        .riskRating(1)
                        .consequences("no consequences")
                        .likelihood("no risk")
                        .correctiveAction("no correctiveAction")
                        .dueDate("1500-09-07")
                        .actionsToImplement("not Immediate")
                        .allocatedTo("not allocatedTo")
                        .factorWorkDescription("no work")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes.name())
                        .furtherSupport(YesNo.Yes.name())
                        .supportPlanUpdate(YesNo.Yes.name())
                        .build();

        assertThrows(
                ReviewServiceException.class,
                () -> {
                    // When
                    incidentReviewService.updateReview(1L, request);
                });
    }
}
