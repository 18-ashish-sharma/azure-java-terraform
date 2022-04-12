package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
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
class IncidentControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private LookupRepository lookupRepository;

    @Autowired private HouseRepository houseRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private IncidentRepository incidentRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser =
                User.builder()
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().name("USER").build())))
                        .build();
        userRepository.save(testUser);

        ODWUserDetails basicUser = new ODWUserDetails(testUser);
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
        incidentRepository.deleteAll();
        houseRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
        lookupRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create incident")
    void testCreateIncident_Staff_Success() {
        // Given
        Lookup category =
                Lookup.builder().name("lookup").lookupType(LookupType.INCIDENT_CATEGORY).build();
        category = lookupRepository.save(category);
        Lookup classification =
                Lookup.builder()
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);
        Lookup type = Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);
        House house = House.builder().houseCode("800").build();
        IncidentRequest request =
                IncidentRequest.builder()
                        .categoryId(category.getId())
                        .classificationId(classification.getId())
                        .typeId(type.getId())
                        .status(Status.RAISED.name())
                        .raisedFor(RaisedFor.STAFF.name())
                        .description("description")
                        .dateOccurred("1980-09-07 10:23:33")
                        .location("loc")
                        .exactLocation("aus")
                        .injuredGivenName("injured1")
                        .injuredFamilyName("injured2")
                        .witnessName("Jessica")
                        .witnessDesignation("first officer")
                        .followUpResponsibility("take care of clients")
                        .policeReport(YesNo.Yes.name())
                        .policeName("NYPD")
                        .policeNumber("911")
                        .policeStation("new york")
                        .beforeIncident("lets try things out!!!")
                        .immediateAction("first aid")
                        .reportableToNDIS(YesNo.Yes.name())
                        .reportableToWorksafe(YesNo.Yes.name())
                        .build();
        // When
        mockMvc.perform(
                        post("/incident/create")
                                .header("Authorization", "Bearer dummy")
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
    @DisplayName("POST /create incident")
    void testCreateIncident_Client_Success() {
        // Given
        Lookup category =
                Lookup.builder().name("lookup").lookupType(LookupType.INCIDENT_CATEGORY).build();
        category = lookupRepository.save(category);
        Lookup classification =
                Lookup.builder()
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);
        Lookup type = Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);
        House house = House.builder().houseCode("800").build();
        house = houseRepository.save(house);
        Client client = Client.builder().name("client").build();
        client = clientRepository.save(client);
        IncidentRequest request =
                IncidentRequest.builder()
                        .categoryId(category.getId())
                        .classificationId(classification.getId())
                        .typeId(type.getId())
                        .houseId(house.getId())
                        .clientId(client.getId())
                        .status(Status.RAISED.name())
                        .raisedFor(RaisedFor.CLIENT.name())
                        .description("description")
                        .dateOccurred("1980-09-07 10:23:33")
                        .location("loc")
                        .exactLocation("aus")
                        .injuredGivenName("injured1")
                        .injuredFamilyName("injured2")
                        .witnessName("Jessica")
                        .witnessDesignation("first officer")
                        .followUpResponsibility("take care of clients")
                        .policeReport(YesNo.Yes.name())
                        .policeName("NYPD")
                        .policeNumber("911")
                        .policeStation("new york")
                        .beforeIncident("lets try things out!!!")
                        .immediateAction("first aid")
                        .reportableToNDIS(YesNo.Yes.name())
                        .reportableToWorksafe(YesNo.Yes.name())
                        .build();
        // When
        mockMvc.perform(
                        post("/incident/create")
                                .header("Authorization", "Bearer dummy")
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
    @DisplayName("POST /create incident when fields are empty")
    void testCreateIncident_WithoutFields() {
        // Given
        long id = 100;
        Lookup lookup = Lookup.builder().id(100).name("lookup").build();
        lookup = lookupRepository.save(lookup);

        House house = House.builder().id(id).houseCode("100").build();
        house = houseRepository.save(house);

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        IncidentRequest request =
                IncidentRequest.builder()
                        .categoryId(lookup.getId())
                        .classificationId(lookup.getId())
                        .typeId(lookup.getId())
                        .houseId(house.getId())
                        .clientId(client.getId())
                        .description("description")
                        .dateOccurred("1980-09-07")
                        .location("loc")
                        .exactLocation("aus")
                        .build();
        // When
        mockMvc.perform(
                        post("/incident/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())
                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get incident")
    void testGetIncident_Success() {
        LocalDateTime date = LocalDateTime.of(1900, 7, 8, 10, 23, 33);
        Lookup category =
                lookupRepository.save(
                        Lookup.builder()
                                .name("category")
                                .lookupType(LookupType.INCIDENT_CATEGORY)
                                .build());

        Lookup classification =
                lookupRepository.save(
                        Lookup.builder()
                                .name("classification")
                                .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                                .build());

        Lookup type =
                lookupRepository.save(
                        Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build());

        House house = houseRepository.save(House.builder().houseCode("801").build());

        Client client = clientRepository.save(Client.builder().name("client").build());

        Incident testIncident = createAnIncident(category, classification, type, client, house);

        mockMvc.perform(
                        get("/incident/get/{id}", testIncident.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testIncident.getId()))
                .andExpect(jsonPath("$.status").value(testIncident.getStatus().name()))
                .andExpect(jsonPath("$.raisedFor").value(testIncident.getRaisedFor().name()))
                .andExpect(jsonPath("$.description").value(testIncident.getDescription()))
                .andExpect(jsonPath("$.escalated").value(true))
                .andExpect(
                        jsonPath("$.escalatedTo")
                                .value(
                                        UserDTO.builder()
                                                .id(testUser.getId())
                                                .email(testUser.getEmail())
                                                .firstName(testUser.getFirstName())
                                                .lastName(testUser.getLastName())
                                                .roleNames(null)
                                                .houses(List.of())
                                                .build()))
                .andExpect(
                        jsonPath("$.dateOccurred")
                                .value(
                                        date.format(
                                                DateTimeFormatter.ofPattern(
                                                        "yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.location").value(testIncident.getLocation()))
                .andExpect(jsonPath("$.exactLocation").value(testIncident.getExactLocation()))
                .andExpect(jsonPath("$.injuredGivenName").value(testIncident.getInjuredGivenName()))
                .andExpect(jsonPath("$.witnessName").value(testIncident.getWitnessName()))
                .andExpect(
                        jsonPath("$.witnessDesignation")
                                .value(testIncident.getWitnessDesignation()))
                .andExpect(
                        jsonPath("$.followUpResponsibility")
                                .value(testIncident.getFollowUpResponsibility()))
                .andExpect(jsonPath("$.policeReport").value(testIncident.getPoliceReport().name()))
                .andExpect(jsonPath("$.policeName").value(testIncident.getPoliceName()))
                .andExpect(jsonPath("$.policeNumber").value(testIncident.getPoliceNumber()))
                .andExpect(jsonPath("$.policeStation").value(testIncident.getPoliceStation()))
                .andExpect(jsonPath("$.beforeIncident").value(testIncident.getBeforeIncident()))
                .andExpect(jsonPath("$.immediateAction").value(testIncident.getImmediateAction()))
                .andExpect(
                        jsonPath("$.reportableToNDIS")
                                .value(testIncident.getReportableToNDIS().name()))
                .andExpect(
                        jsonPath("$.reportableToWorksafe")
                                .value(testIncident.getReportableToWorksafe().name()))
                .andExpect(
                        jsonPath("$.injuredFamilyName").value(testIncident.getInjuredFamilyName()))
                .andExpect(
                        jsonPath("$.reportedBy")
                                .value(
                                        UserDTO.builder()
                                                .id(testUser.getId())
                                                .email(testUser.getEmail())
                                                .firstName(testUser.getFirstName())
                                                .lastName(testUser.getLastName())
                                                .roleNames(null)
                                                .houses(List.of())
                                                .build()))
                .andExpect(
                        jsonPath("$.category")
                                .value(
                                        LookupDTO.builder()
                                                .id(category.getId())
                                                .name(category.getName())
                                                .build()))
                .andExpect(
                        jsonPath("$.type")
                                .value(
                                        LookupDTO.builder()
                                                .id(type.getId())
                                                .name(type.getName())
                                                .build()))
                .andExpect(
                        jsonPath("$.classification")
                                .value(
                                        LookupDTO.builder()
                                                .id(classification.getId())
                                                .name(classification.getName())
                                                .build()))
                .andExpect(
                        jsonPath("$.house")
                                .value(
                                        HouseDTO.builder()
                                                .id(house.getId())
                                                .houseCode(house.getHouseCode())
                                                .build()))
                .andExpect(
                        jsonPath("$.client")
                                .value(
                                        ClientDTO.builder()
                                                .id(client.getId())
                                                .name(client.getName())
                                                .build()));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /list incidents")
    void testListIncidents() {
        // Given
        Lookup category =
                lookupRepository.save(
                        Lookup.builder()
                                .name("category")
                                .lookupType(LookupType.INCIDENT_CATEGORY)
                                .build());

        Lookup classification =
                lookupRepository.save(
                        Lookup.builder()
                                .name("classification")
                                .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                                .build());

        Lookup type =
                lookupRepository.save(
                        Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build());

        House house = houseRepository.save(House.builder().houseCode("802").build());

        Client client = clientRepository.save(Client.builder().name("client").build());

        Incident testIncident = createAnIncident(category, classification, type, client, house);

        ListIncidentRequest request =
                ListIncidentRequest.builder()
                        .clientName(client.getName())
                        .houseCode(house.getHouseCode())
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        mockMvc.perform(
                        post("/incident/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(testIncident.getId()))
                .andExpect(
                        jsonPath("$.[0].house")
                                .value(
                                        HouseDTO.builder()
                                                .id(house.getId())
                                                .houseCode(house.getHouseCode())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].client")
                                .value(
                                        ClientDTO.builder()
                                                .id(client.getId())
                                                .name(client.getName())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].category")
                                .value(
                                        LookupDTO.builder()
                                                .id(category.getId())
                                                .name(category.getName())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].type")
                                .value(
                                        LookupDTO.builder()
                                                .id(type.getId())
                                                .name(type.getName())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].classification")
                                .value(
                                        LookupDTO.builder()
                                                .id(classification.getId())
                                                .name(classification.getName())
                                                .build()))
                .andExpect(jsonPath("$.[0].status").value(testIncident.getStatus().name()))
                .andExpect(jsonPath("$.[0].raisedFor").value(testIncident.getRaisedFor().name()))
                .andExpect(jsonPath("$.[0].escalated").value(true))
                .andExpect(jsonPath("$.[0].witnessName").value(testIncident.getWitnessName()))
                .andExpect(
                        jsonPath("$.[0].witnessDesignation")
                                .value(testIncident.getWitnessDesignation()))
                .andExpect(
                        jsonPath("$.[0].followUpResponsibility")
                                .value(testIncident.getFollowUpResponsibility()))
                .andExpect(
                        jsonPath("$.[0].policeReport").value(testIncident.getPoliceReport().name()))
                .andExpect(jsonPath("$.[0].policeName").value(testIncident.getPoliceName()))
                .andExpect(jsonPath("$.[0].policeNumber").value(testIncident.getPoliceNumber()))
                .andExpect(jsonPath("$.[0].policeStation").value(testIncident.getPoliceStation()))
                .andExpect(jsonPath("$.[0].beforeIncident").value(testIncident.getBeforeIncident()))
                .andExpect(
                        jsonPath("$.[0].immediateAction").value(testIncident.getImmediateAction()))
                .andExpect(
                        jsonPath("$.[0].reportableToNDIS")
                                .value(testIncident.getReportableToNDIS().name()))
                .andExpect(
                        jsonPath("$.[0].reportableToWorksafe")
                                .value(testIncident.getReportableToWorksafe().name()))
                .andExpect(
                        jsonPath("$.[0].reportedBy")
                                .value(
                                        UserDTO.builder()
                                                .id(testUser.getId())
                                                .email(testUser.getEmail())
                                                .firstName(testUser.getFirstName())
                                                .lastName(testUser.getLastName())
                                                .roleNames(null)
                                                .houses(List.of())
                                                .build()))
                .andExpect(
                        jsonPath("$.[0].escalatedTo")
                                .value(
                                        UserDTO.builder()
                                                .id(testUser.getId())
                                                .email(testUser.getEmail())
                                                .firstName(testUser.getFirstName())
                                                .lastName(testUser.getLastName())
                                                .roleNames(null)
                                                .houses(List.of())
                                                .build()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PATCH /close incident")
    void testCloseIncident_Success() {
        // Given
        String closedBy = "merin";
        Lookup category =
                lookupRepository.save(
                        Lookup.builder()
                                .name("category")
                                .lookupType(LookupType.INCIDENT_CATEGORY)
                                .build());

        Lookup classification =
                lookupRepository.save(
                        Lookup.builder()
                                .name("classification")
                                .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                                .build());

        Lookup type =
                lookupRepository.save(
                        Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build());

        House house = houseRepository.save(House.builder().houseCode("802").build());

        Client client = clientRepository.save(Client.builder().name("client").build());

        Incident testIncident = createAnIncident(category, classification, type, client, house);

        // When
        mockMvc.perform(
                        patch("/incident/close/{id}/{closedBy}", testIncident.getId(), closedBy)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PATCH /escalate incident")
    void testEscalateIncident_Success() {
        // Given
        Lookup category =
                lookupRepository.save(
                        Lookup.builder()
                                .name("category")
                                .lookupType(LookupType.INCIDENT_CATEGORY)
                                .build());

        Lookup classification =
                lookupRepository.save(
                        Lookup.builder()
                                .name("classification")
                                .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                                .build());

        Lookup type =
                lookupRepository.save(
                        Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build());

        House house = houseRepository.save(House.builder().houseCode("802").build());

        Client client = clientRepository.save(Client.builder().name("client").build());

        Incident testIncident = createAnIncident(category, classification, type, client, house);

        // When
        mockMvc.perform(
                        patch(
                                        "/incident/escalate/{id}/{userId}",
                                        testIncident.getId(),
                                        testUser.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    private Incident createAnIncident(
            Lookup category, Lookup classification, Lookup type, Client client, House house) {
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
        LocalDateTime date = LocalDateTime.of(1900, 7, 8, 10, 23, 33);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.STAFF)
                        .dateOccurred(date)
                        .description(description)
                        .location(location)
                        .reportedBy(testUser)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .escalated(true)
                        .escalatedTo(testUser)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.Yes)
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.Yes)
                        .reportableToWorksafe(YesNo.Yes)
                        .build();
        incident = incidentRepository.save(incident);

        return incident;
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update incident")
    void testUpdateIncident_Client_Success() {

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

        String houseCode = "101";
        House house = House.builder().id(1).houseCode(houseCode).build();
        house = houseRepository.save(house);

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        Lookup category =
                Lookup.builder()
                        .id(1)
                        .name("category")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        category = lookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .id(1)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);

        Lookup type =
                Lookup.builder().id(1).name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.CLIENT)
                        .dateOccurred(LocalDateTime.of(1966, 5, 6, 10, 23, 33))
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
                        .reportableToWorksafe(YesNo.No)
                        .reportedBy(testUser)
                        .escalated(true)
                        .escalatedTo(testUser)
                        .build();
        incident = incidentRepository.save(incident);

        IncidentRequest request =
                IncidentRequest.builder()
                        .id(incident.getId())
                        .categoryId(category.getId())
                        .typeId(type.getId())
                        .classificationId(classification.getId())
                        .houseId(house.getId())
                        .clientId(client.getId())
                        .status(Status.CLOSED.name())
                        .raisedFor(RaisedFor.CLIENT.name())
                        .description(description)
                        .dateOccurred("1900-09-12 10:23:33")
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.Yes.name())
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.Yes.name())
                        .reportableToWorksafe(YesNo.Yes.name())
                        .build();

        // When
        mockMvc.perform(
                        put("/incident/update/{id}", request.getId())
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
    @DisplayName("PUT /update incident")
    void testUpdateIncident_Staff_Success() {

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

        String houseCode = "101";
        House house = House.builder().id(1).houseCode(houseCode).build();
        house = houseRepository.save(house);

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        Lookup category =
                Lookup.builder()
                        .id(1)
                        .name("category")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        category = lookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .id(1)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);

        Lookup type =
                Lookup.builder().id(1).name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.STAFF)
                        .dateOccurred(LocalDateTime.of(1966, 5, 6, 10, 23, 33))
                        .description("description")
                        .location("location")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .reportedBy(testUser)
                        .escalated(true)
                        .escalatedTo(testUser)
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
                        .reportableToWorksafe(YesNo.No)
                        .build();
        incident = incidentRepository.save(incident);

        IncidentRequest request =
                IncidentRequest.builder()
                        .id(incident.getId())
                        .categoryId(category.getId())
                        .typeId(type.getId())
                        .classificationId(classification.getId())
                        .houseId(house.getId())
                        .clientId(client.getId())
                        .status(Status.CLOSED.name())
                        .raisedFor(RaisedFor.STAFF.name())
                        .description(description)
                        .dateOccurred("1900-09-12 10:23:33")
                        .location(location)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .witnessName(witnessName)
                        .witnessDesignation(witnessDesignation)
                        .followUpResponsibility(followUpResponsibility)
                        .policeReport(YesNo.Yes.name())
                        .policeName(policeName)
                        .policeNumber(policeNumber)
                        .policeStation(policeStation)
                        .beforeIncident(beforeIncident)
                        .immediateAction(immediateAction)
                        .reportableToNDIS(YesNo.Yes.name())
                        .reportableToWorksafe(YesNo.Yes.name())
                        .build();

        // When
        mockMvc.perform(
                        put("/incident/update/{id}", request.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update incident Failure")
    void testUpdateIncident_Failure_Incident_NotPresent() {

        String houseCode = "101";
        House house = House.builder().id(1).houseCode(houseCode).build();
        house = houseRepository.save(house);

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        Lookup category =
                Lookup.builder()
                        .id(1)
                        .name("category")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        category = lookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .id(1)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);

        Lookup type =
                Lookup.builder().id(1).name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.STAFF)
                        .dateOccurred(LocalDateTime.of(1966, 5, 6, 10, 23, 33))
                        .description("description")
                        .location("location")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .witnessName("Jessica")
                        .witnessDesignation("first officer")
                        .followUpResponsibility("take care of clients")
                        .policeReport(YesNo.valueOf(YesNo.Yes.name()))
                        .policeName("NYPD")
                        .policeNumber("911")
                        .policeStation("new york")
                        .beforeIncident("lets try things out!!!")
                        .immediateAction("first aid")
                        .reportableToNDIS(YesNo.valueOf(YesNo.Yes.name()))
                        .reportableToWorksafe(YesNo.valueOf("Yes"))
                        .reportedBy(testUser)
                        .escalated(true)
                        .escalatedTo(testUser)
                        .build();
        incident = incidentRepository.save(incident);

        IncidentRequest request =
                IncidentRequest.builder()
                        .id(incident.getId())
                        .categoryId(category.getId())
                        .typeId(type.getId())
                        .classificationId(classification.getId())
                        .houseId(house.getId())
                        .clientId(client.getId())
                        .status(Status.CLOSED.name())
                        .raisedFor(RaisedFor.CLIENT.name())
                        .description("description")
                        .dateOccurred("1900-09-12")
                        .location("loc")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .build();
        // When
        mockMvc.perform(
                        put("/incident/update/{id}", 1000)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /list for graph")
    void testListForGraph() {
        // Given
        Instant createdAt = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localCreatedAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC);

        String houseCode = "101";
        House house = House.builder().id(1).houseCode(houseCode).build();
        house = houseRepository.save(house);

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        Lookup category =
                Lookup.builder()
                        .id(1)
                        .name("category")
                        .lookupType(LookupType.INCIDENT_CATEGORY)
                        .build();
        category = lookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .id(1)
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);

        Lookup type =
                Lookup.builder().id(1).name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);

        Incident incident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .raisedFor(RaisedFor.CLIENT)
                        .dateOccurred(LocalDateTime.of(1966, 5, 6, 10, 23, 33))
                        .description("description")
                        .location("location")
                        .exactLocation("exactLocation")
                        .injuredGivenName("injuredGivenName")
                        .injuredFamilyName("injuredFamilyName")
                        .reportedBy(testUser)
                        .createdAt(createdAt)
                        .escalated(false)
                        .escalatedTo(testUser)
                        .build();
        incident = incidentRepository.save(incident);

        ListForGraphRequest request =
                ListForGraphRequest.builder()
                        .raisedFor(RaisedFor.CLIENT.name())
                        .start(createdAt.minus(5, ChronoUnit.MINUTES))
                        .end(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();

        // When
        mockMvc.perform(
                        post("/incident/listForGraph")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[0].id").value(incident.getId()))
                .andExpect(jsonPath("$.[0].raisedFor").value(RaisedFor.CLIENT.name()))
                .andExpect(jsonPath("$.[0].description").value(incident.getDescription()))
                .andExpect(jsonPath("$.[0].escalated").value(false))
                .andExpect(jsonPath("$.[0].status").value(Status.RAISED.name()))
                .andExpect(jsonPath("$.[0].createdAt").value(localCreatedAt.format(formatter)));
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
