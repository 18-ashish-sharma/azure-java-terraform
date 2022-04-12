package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.IncidentServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
import java.time.Instant;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {
    @Mock IncidentRepository mockIncidentRepository;

    @Mock ClientRepository mockClientRepository;

    @Mock LookupRepository mockLookupRepository;

    @Mock HouseRepository mockHouseRepository;

    @Mock UserRepository mockUserRepository;

    private IncidentService incidentService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        incidentService =
                new IncidentService(
                        mockIncidentRepository,
                        mockClientRepository,
                        mockHouseRepository,
                        mockLookupRepository,
                        mockUserRepository,
                        context);
    }

    @SneakyThrows
    @Test
    void testCreateIncident_ForStaff_Success() {
        // Given
        long id = 1;
        String status = "RAISED";
        String raisedFor = "STAFF";
        String date = "1980-09-08 10:23:33";
        String description = "desc";
        String location = "loc";
        String exactLocation = "ex-loc";
        String injuredGivenName = "injured1";
        String injuredFamilyName = "injured2";
        String witnessName = "james";
        String witnessDesignation = "Captian";
        String followUpResponsibility = "Leadership";
        String policeName = "LAPD";
        String policeNumber = "100";
        String policeStation = "Los Angeles";
        String beforeIncident = "what happened!!";
        String immediateAction = " aid";
        String policeReport = "Yes";
        String reportableToNDIS = "Yes";
        String reportableToWorksafe = "Yes";

        IncidentRequest request =
                IncidentRequest.builder()
                        .categoryId(1L)
                        .classificationId(2L)
                        .typeId(3L)
                        .status(status)
                        .raisedFor(raisedFor)
                        .dateOccurred(date)
                        .description(description)
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(policeReport)
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(reportableToNDIS)
                        .reportableToWorksafe(reportableToWorksafe)
                        .build();
        Lookup category =
                Lookup.builder()
                        .id(1L)
                        .name("lookup")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        Lookup classification =
                Lookup.builder()
                        .id(2L)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        Lookup type =
                Lookup.builder().id(3L).name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        User user = User.builder().email(context.currentUser()).id(id).build();
        when(mockLookupRepository.getById(1L)).thenReturn(category);
        when(mockLookupRepository.getById(2L)).thenReturn(classification);
        when(mockLookupRepository.getById(3L)).thenReturn(type);
        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);
        // When
        incidentService.createIncident(request);
        // Then
        Incident expected =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.STAFF)
                        .createdAt(context.now())
                        .dateOccurred(
                                LocalDateTime.parse(
                                        date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .description(description)
                        .location(location)
                        .exactLocation(exactLocation)
                        .reportedBy(user)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .reviewedBy("")
                        .closedBy("")
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.valueOf(policeReport))
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.valueOf(reportableToNDIS))
                        .reportableToWorksafe(YesNo.valueOf(reportableToWorksafe))
                        .build();
        ArgumentCaptor<Incident> incidentArgumentCaptor = ArgumentCaptor.forClass(Incident.class);
        verify(mockIncidentRepository).save(incidentArgumentCaptor.capture());
        Incident actual = incidentArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testCreateIncident_ForClient_Success() {
        // Given
        long id = 1;
        String status = "RAISED";
        String raisedFor = "CLIENT";
        String date = "1980-09-08 10:23:33";
        String description = "desc";
        String location = "loc";
        String exactLocation = "ex-loc";
        String injuredGivenName = "injured1";
        String injuredFamilyName = "injured2";
        String witnessName = "james";
        String witnessDesignation = "Captian";
        String followUpResponsibility = "Leadership";
        String policeName = "LAPD";
        String policeNumber = "100";
        String policeStation = "Los Angeles";
        String beforeIncident = "what happened!!";
        String immediateAction = " aid";
        String policeReport = "Yes";
        String reportableToNDIS = "Yes";
        String reportableToWorksafe = "Yes";
        IncidentRequest request =
                IncidentRequest.builder()
                        .categoryId(1L)
                        .classificationId(2L)
                        .typeId(3L)
                        .clientId(1L)
                        .houseId(1L)
                        .status(status)
                        .raisedFor(raisedFor)
                        .dateOccurred(date)
                        .description(description)
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(policeReport)
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(reportableToNDIS)
                        .reportableToWorksafe(reportableToWorksafe)
                        .build();
        Lookup category =
                Lookup.builder()
                        .id(1L)
                        .name("lookup")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        Lookup classification =
                Lookup.builder()
                        .id(2L)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        Lookup type =
                Lookup.builder().id(3L).name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        House house = House.builder().id(id).houseCode("100").build();
        Client client = Client.builder().id(id).name("client").build();
        User user = User.builder().email(context.currentUser()).id(id).build();
        when(mockLookupRepository.getById(1L)).thenReturn(category);
        when(mockLookupRepository.getById(2L)).thenReturn(classification);
        when(mockLookupRepository.getById(3L)).thenReturn(type);
        when(mockHouseRepository.getById(id)).thenReturn(house);
        when(mockClientRepository.getById(id)).thenReturn(client);
        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);
        // When
        incidentService.createIncident(request);
        // Then
        Incident expected =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.CLIENT)
                        .createdAt(context.now())
                        .dateOccurred(
                                LocalDateTime.parse(
                                        date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .description(description)
                        .location(location)
                        .exactLocation(exactLocation)
                        .reportedBy(user)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .closedBy("")
                        .reviewedBy("")
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.valueOf(policeReport))
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.valueOf(reportableToNDIS))
                        .reportableToWorksafe(YesNo.valueOf(reportableToWorksafe))
                        .build();
        ArgumentCaptor<Incident> incidentArgumentCaptor = ArgumentCaptor.forClass(Incident.class);
        verify(mockIncidentRepository).save(incidentArgumentCaptor.capture());
        Incident actual = incidentArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetIncident_Success() {
        // Given
        Incident incident = createAnIncident();

        IncidentDTO expected = new ModelMapper().map(incident, IncidentDTO.class);

        when(mockIncidentRepository.getById(ArgumentMatchers.anyLong())).thenReturn(incident);

        // When
        IncidentDTO actual = incidentService.getIncidentById(1L);

        // Then
        assertEquals(actual, expected);
    }

    @Test
    void testGetIncident_Failure_IncidentNotFound() {
        // Given
        long id = 1;

        assertThrows(
                IncidentServiceException.class,
                () -> {
                    // When
                    incidentService.getIncidentById(id);
                });
    }

    @Test
    @SneakyThrows
    void testListIncidents() {
        String houseCode = "100";
        String clientName = "client";
        Incident incident1 = createAnIncident();
        Incident incident2 = createAnIncident();
        incident2.setId(2);

        User user = User.builder().email(context.currentUser()).id(1).build();

        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);
        when(mockIncidentRepository.findAllByHouse_HouseCodeAndClient_Name(
                        any(String.class), any(String.class), any(Pageable.class)))
                .thenReturn(List.of(incident1, incident2));
        ListIncidentRequest request =
                ListIncidentRequest.builder()
                        .clientName(clientName)
                        .houseCode(houseCode)
                        .pageNumber(0)
                        .pageSize(2)
                        .build();

        List<IncidentDTO> incidents = incidentService.listIncident(request);

        assertEquals(2, incidents.size());
        assertThat(
                incidents,
                (allOf(
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "house",
                                        equalTo(
                                                HouseDTO.builder()
                                                        .id(1)
                                                        .houseCode("100")
                                                        .build()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "client",
                                        equalTo(ClientDTO.builder().id(1).name("client").build()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "reportedBy",
                                        equalTo(
                                                UserDTO.builder()
                                                        .id(1)
                                                        .email("test@test.com")
                                                        .build()))),
                        everyItem(HasPropertyWithValue.hasProperty("status", equalTo("RAISED"))),
                        everyItem(HasPropertyWithValue.hasProperty("raisedFor", equalTo("STAFF"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "witnessName", equalTo(incident1.getWitnessName()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "witnessDesignation",
                                        equalTo(incident1.getWitnessDesignation()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "followUpResponsibility",
                                        equalTo(incident1.getFollowUpResponsibility()))),
                        everyItem(HasPropertyWithValue.hasProperty("policeReport", equalTo("Yes"))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "policeName", equalTo(incident1.getPoliceName()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "policeStation", equalTo(incident1.getPoliceStation()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "policeNumber", equalTo(incident1.getPoliceNumber()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "beforeIncident", equalTo(incident1.getBeforeIncident()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "immediateAction",
                                        equalTo(incident1.getImmediateAction()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "reportableToNDIS",
                                        equalTo(incident1.getReportableToNDIS().name()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "reportableToWorksafe",
                                        equalTo(incident1.getReportableToWorksafe().name()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "category",
                                        equalTo(LookupDTO.builder().id(1).name("test").build()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "type",
                                        equalTo(LookupDTO.builder().id(1).name("test").build()))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "classification",
                                        equalTo(LookupDTO.builder().id(1).name("test").build()))),
                        everyItem(HasPropertyWithValue.hasProperty("escalated", equalTo(true))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "escalatedTo",
                                        equalTo(
                                                UserDTO.builder()
                                                        .id(1)
                                                        .email("test@test.com")
                                                        .build()))))));
    }

    @SneakyThrows
    @Test
    void testListAllIncidents_Success_Empty() {
        // Given
        User user = User.builder().email(context.currentUser()).id(1).build();
        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);

        ListIncidentRequest request =
                ListIncidentRequest.builder()
                        .clientName("clientName")
                        .houseCode("houseCode")
                        .pageNumber(0)
                        .pageSize(2)
                        .build();

        // When
        List<IncidentDTO> incidentListDTOS = incidentService.listIncident(request);

        // Then
        assertEquals(incidentListDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    void testCloseIncident_Success() {
        Incident incident = createAnIncident();

        when(mockIncidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        // When
        incidentService.closeIncident(1L, "Abraham");

        Incident expected = createAnIncident();
        expected.setStatus(Status.CLOSED);
        expected.setClosedBy("Abraham");

        // Then
        verify(mockIncidentRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    void testEscalateIncident_Success() {
        Incident incident = createAnIncident();
        User user = User.builder().id(1).email("test@test.com").build();
        when(mockIncidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        incidentService.escalateIssue(1L, 1L);

        Incident expected = createAnIncident();
        expected.setEscalated(true);
        expected.setEscalatedTo(user);

        // Then
        verify(mockIncidentRepository).save(eq(expected));
    }

    private Incident createAnIncident() {
        User user = User.builder().email(context.currentUser()).id(1).build();

        Client client = Client.builder().id(1).name("client").build();
        House house = House.builder().id(1).houseCode("100").build();
        Lookup lookup = Lookup.builder().id(1).name("test").build();
        LocalDateTime dob = LocalDateTime.of(1900, 7, 8, 10, 23, 33);

        return Incident.builder()
                .id(1)
                .status(Status.RAISED)
                .raisedFor(RaisedFor.STAFF)
                .description("Injured")
                .escalated(true)
                .escalatedTo(user)
                .dateOccurred(dob)
                .location("sydney")
                .exactLocation("Australia")
                .injuredGivenName("Tom")
                .injuredFamilyName("jacob")
                .witnessName("Jessica")
                .witnessDesignation("first officer")
                .followUpResponsibility("take care of clients")
                .policeReport(YesNo.Yes)
                .policeName("NYPD")
                .policeNumber("911")
                .policeStation("new york")
                .beforeIncident("lets try things out!!!")
                .immediateAction("first aid")
                .reportableToNDIS(YesNo.Yes)
                .reportableToWorksafe(YesNo.Yes)
                .reportedBy(user)
                .category(lookup)
                .type(lookup)
                .classification(lookup)
                .house(house)
                .client(client)
                .build();
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateIncident_ForClient_Success() {
        // Given

        String description = "desc";
        String location = "loc";
        String exactLocation = "ex-loc";
        String injuredGivenName = "injured1";
        String injuredFamilyName = "injured2";

        LocalDateTime date = LocalDateTime.of(1900, 7, 8, 10, 23, 33);
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
        house = mockHouseRepository.save(house);

        Client client = Client.builder().name("client").build();
        client = mockClientRepository.save(client);
        User user = User.builder().email("test@test.com").password("test").build();
        user = mockUserRepository.save(user);

        Lookup category =
                Lookup.builder()
                        .id(19)
                        .name("category")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        category = mockLookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .id(12)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = mockLookupRepository.save(classification);

        Lookup type =
                Lookup.builder().id(13).name("type").lookupType(LookupType.INCIDENT_TYPE).build();

        String witnessName = "james";
        String witnessDesignation = "Captian";
        String followUpResponsibility = "Leadership";
        String policeName = "LAPD";
        String policeNumber = "100";
        String policeStation = "Los Angeles";
        String beforeIncident = "what happened!!";
        String immediateAction = " aid";
        String policeReport = "Yes";
        String reportableToNDIS = "Yes";
        String reportableToWorksafe = "Yes";

        type = mockLookupRepository.save(type);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.CLIENT)
                        .dateOccurred(LocalDateTime.of(1967, 4, 3, 10, 23, 33))
                        .description("description")
                        .location("location")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .witnessName("Jessica")
                        .witnessDesignation("first officer")
                        .followUpResponsibility("take care of clients")
                        .policeReport(YesNo.Yes)
                        .policeName("NYPD")
                        .policeNumber("911")
                        .policeStation("new york")
                        .beforeIncident("lets try things out!!!")
                        .immediateAction("first aid")
                        .reportableToNDIS(YesNo.Yes)
                        .reportableToWorksafe(YesNo.Yes)
                        .escalated(true)
                        .escalatedTo(user)
                        .build();

        IncidentRequest request =
                IncidentRequest.builder()
                        .id(1L)
                        .categoryId(1L)
                        .typeId(2L)
                        .classificationId(3L)
                        .houseId(4L)
                        .clientId(5L)
                        .status(Status.CLOSED.name())
                        .raisedFor(RaisedFor.CLIENT.name())
                        .description(description)
                        .dateOccurred("1900-07-08 10:23:33")
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(policeReport)
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(reportableToNDIS)
                        .reportableToWorksafe(reportableToWorksafe)
                        .build();

        when(mockIncidentRepository.findById(request.getId())).thenReturn(Optional.of(incident));

        // when
        incidentService.updateIncident(1L, request);

        Incident expected =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.CLOSED)
                        .raisedFor(RaisedFor.CLIENT)
                        .dateOccurred(date)
                        .description(description)
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.valueOf(policeReport))
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.valueOf(reportableToNDIS))
                        .reportableToWorksafe(YesNo.valueOf(reportableToWorksafe))
                        .escalated(true)
                        .escalatedTo(user)
                        .build();
        // then
        verify(mockIncidentRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateIncident_ForStaff_Success() {
        // Given

        String description = "desc";
        String location = "loc";
        String exactLocation = "ex-loc";
        String injuredGivenName = "injured1";
        String injuredFamilyName = "injured2";

        LocalDateTime date = LocalDateTime.of(1900, 7, 8, 10, 23, 33);
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
        house = mockHouseRepository.save(house);

        Client client = Client.builder().name("client").build();
        client = mockClientRepository.save(client);
        User user = User.builder().email("test@test.com").password("test").build();
        user = mockUserRepository.save(user);

        Lookup category =
                Lookup.builder()
                        .id(19)
                        .name("category")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        category = mockLookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .id(12)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = mockLookupRepository.save(classification);

        Lookup type =
                Lookup.builder().id(13).name("type").lookupType(LookupType.INCIDENT_TYPE).build();

        String witnessName = "james";
        String witnessDesignation = "Captian";
        String followUpResponsibility = "Leadership";
        String policeName = "LAPD";
        String policeNumber = "100";
        String policeStation = "Los Angeles";
        String beforeIncident = "what happened!!";
        String immediateAction = " aid";
        String policeReport = "Yes";
        String reportableToNDIS = "Yes";
        String reportableToWorksafe = "Yes";
        type = mockLookupRepository.save(type);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.STAFF)
                        .dateOccurred(LocalDateTime.of(1967, 4, 3, 10, 23, 33))
                        .description("description")
                        .location("location")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .witnessName("Jessica")
                        .witnessDesignation("first officer")
                        .followUpResponsibility("take care of clients")
                        .policeReport(YesNo.Yes)
                        .policeName("NYPD")
                        .policeNumber("911")
                        .policeStation("new york")
                        .beforeIncident("lets try things out!!!")
                        .immediateAction("first aid")
                        .reportableToNDIS(YesNo.Yes)
                        .reportableToWorksafe(YesNo.Yes)
                        .escalated(true)
                        .escalatedTo(user)
                        .build();

        IncidentRequest request =
                IncidentRequest.builder()
                        .id(1L)
                        .categoryId(1L)
                        .typeId(2L)
                        .classificationId(3L)
                        .houseId(4L)
                        .clientId(5L)
                        .status(Status.CLOSED.name())
                        .raisedFor(RaisedFor.STAFF.name())
                        .description(description)
                        .dateOccurred("1900-07-08 10:23:33")
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(policeReport)
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(reportableToNDIS)
                        .reportableToWorksafe(reportableToWorksafe)
                        .build();

        when(mockIncidentRepository.findById(request.getId())).thenReturn(Optional.of(incident));

        // when
        incidentService.updateIncident(1L, request);

        Incident expected =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.CLOSED)
                        .raisedFor(RaisedFor.STAFF)
                        .dateOccurred(date)
                        .description(description)
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.valueOf(policeReport))
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.valueOf(reportableToNDIS))
                        .reportableToWorksafe(YesNo.valueOf(reportableToWorksafe))
                        .escalated(true)
                        .escalatedTo(user)
                        .build();
        // then
        verify(mockIncidentRepository).save(eq(expected));
    }

    @Test
    void testUpdateIncident_Failure_IncidentNotFound() {
        // Given

        IncidentRequest request =
                IncidentRequest.builder()
                        .id(1L)
                        .categoryId(1L)
                        .typeId(2L)
                        .classificationId(3L)
                        .houseId(4L)
                        .clientId(5L)
                        .status(Status.CLOSED.name())
                        .raisedFor(RaisedFor.CLIENT.name())
                        .description("description")
                        .dateOccurred("1900-07-08")
                        .location("location")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .build();

        assertThrows(
                IncidentServiceException.class,
                () -> {
                    // When
                    incidentService.updateIncident(1L, request);
                });
    }

    @SneakyThrows
    @Test
    void testListForGraph_Success() {
        Instant now = Instant.now();

        Incident incident1 =
                Incident.builder()
                        .id(1)
                        .raisedFor(RaisedFor.CLIENT)
                        .description("description")
                        .escalated(false)
                        .status(Status.INACTIVE)
                        .createdAt(context.now())
                        .build();
        Incident incident2 =
                Incident.builder()
                        .id(2)
                        .raisedFor(RaisedFor.STAFF)
                        .description("descriptions")
                        .escalated(false)
                        .status(Status.INACTIVE)
                        .createdAt(context.now())
                        .build();

        when(mockIncidentRepository.findAllByRaisedForAndCreatedAtBetweenAndStatusNot(
                        any(), any(), any(), any()))
                .thenReturn(List.of(incident1, incident2));
        ListForGraphRequest request =
                ListForGraphRequest.builder()
                        .raisedFor(RaisedFor.CLIENT.name())
                        .start(now)
                        .end(now)
                        .build();
        // When
        List<ListGraphDTO> listGraphDTOS = incidentService.listForGraph(request);

        // Then
        assertEquals(listGraphDTOS.size(), 2);
        assertThat(
                listGraphDTOS,
                (allOf(
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "id", AnyOf.anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "raisedFor", anyOf(equalTo("CLIENT"), equalTo("STAFF")))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "description",
                                        anyOf(
                                                equalTo(incident1.getDescription()),
                                                equalTo(incident2.getDescription())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "status", anyOf(equalTo("INACTIVE")))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createdAt",
                                        anyOf(
                                                equalTo(incident1.getCreatedAt()),
                                                equalTo(incident2.getCreatedAt())))))));
    }

    @SneakyThrows
    @Test
    void testListForGraph_Success_Empty() {
        // Given
        ListForGraphRequest request =
                ListForGraphRequest.builder()
                        .start(Instant.now())
                        .end(Instant.now())
                        .raisedFor(RaisedFor.CLIENT.name())
                        .build();

        // When
        List<ListGraphDTO> listGraphDTOS = incidentService.listForGraph(request);

        // Then
        assertEquals(listGraphDTOS.size(), 0);
    }
}
