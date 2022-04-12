package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedoorway.project.dto.IncidentReviewDTO;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class IncidentReviewControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private IncidentReviewRepository incidentReviewRepository;

    @Autowired private IncidentRepository incidentRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private LookupRepository lookupRepository;

    @Autowired private HouseRepository houseRepository;

    @Autowired private ClientRepository clientRepository;

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
        incidentReviewRepository.deleteAll();
        incidentRepository.deleteAll();
        houseRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
        lookupRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET /get review")
    void testGetReview_Success() {
        // Given
        Incident incident = createAnIncident();
        IncidentReview review = createAReview(incident);

        // When
        mockMvc.perform(
                        get("/incident-review/get/{incidentId}", review.getIncident().getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].incidentId").value(review.getIncident().getId()))
                .andExpect(jsonPath("$.[0].factorHouseDesign").value(review.getFactorHouseDesign()))
                .andExpect(
                        jsonPath("$.[0].factorHouseEquipment")
                                .value(review.getFactorHouseEquipment()))
                .andExpect(jsonPath("$.[0].factorHouseFumes").value(review.getFactorHouseFumes()))
                .andExpect(jsonPath("$.[0].factorHouseGuards").value(review.getFactorHouseGuards()))
                .andExpect(jsonPath("$.[0].factorHouseOther").value(review.getFactorHouseOther()))
                .andExpect(
                        jsonPath("$.[0].factorHouseUnsuitable")
                                .value(review.getFactorHouseUnsuitable()))
                .andExpect(
                        jsonPath("$.[0].factorHouseDescription")
                                .value(review.getFactorHouseDescription()))
                .andExpect(
                        jsonPath("$.[0].factorHouseWhatHappened")
                                .value(review.getFactorHouseWhatHappened()))
                .andExpect(jsonPath("$.[0].factorPersonName").value(review.getFactorPersonName()))
                .andExpect(
                        jsonPath("$.[0].factorPersonAlcohol")
                                .value(review.getFactorPersonAlcohol()))
                .andExpect(
                        jsonPath("$.[0].factorPersonComplacency")
                                .value(review.getFactorPersonComplacency()))
                .andExpect(
                        jsonPath("$.[0].factorPersonLineOfFire")
                                .value(review.getFactorPersonLineOfFire()))
                .andExpect(
                        jsonPath("$.[0].factorPersonFrustration")
                                .value(review.getFactorPersonFrustration()))
                .andExpect(
                        jsonPath("$.[0].factorPersonLackTraining")
                                .value(review.getFactorPersonLackTraining()))
                .andExpect(
                        jsonPath("$.[0].factorPersonLossBalance")
                                .value(review.getFactorPersonLossBalance()))
                .andExpect(
                        jsonPath("$.[0].factorPersonManualHandling")
                                .value(review.getFactorPersonManualHandling()))
                .andExpect(
                        jsonPath("$.[0].factorPersonMedicalCondition")
                                .value(review.getFactorPersonMedicalCondition()))
                .andExpect(jsonPath("$.[0].factorPersonNoPPE").value(review.getFactorPersonNoPPE()))
                .andExpect(jsonPath("$.[0].factorPersonOther").value(review.getFactorPersonOther()))
                .andExpect(
                        jsonPath("$.[0].factorPersonRushing")
                                .value(review.getFactorPersonRushing()))
                .andExpect(
                        jsonPath("$.[0].factorPersonThreatening")
                                .value(review.getFactorPersonThreatening()))
                .andExpect(
                        jsonPath("$.[0].factorPersonFatigue")
                                .value(review.getFactorPersonFatigue()))
                .andExpect(
                        jsonPath("$.[0].factorPersonUnsafePractice")
                                .value(review.getFactorPersonUnsafePractice()))
                .andExpect(
                        jsonPath("$.[0].factorPersonWorkingAlone")
                                .value(review.getFactorPersonWorkingAlone()))
                .andExpect(
                        jsonPath("$.[0].factorPersonDescription")
                                .value(review.getFactorPersonDescription()))
                .andExpect(
                        jsonPath("$.[0].factorPersonWhatHappened")
                                .value(review.getFactorPersonWhatHappened()))
                .andExpect(jsonPath("$.[0].factorEnvAnimals").value(review.getFactorEnvAnimals()))
                .andExpect(jsonPath("$.[0].factorEnvConfine").value(review.getFactorEnvConfine()))
                .andExpect(jsonPath("$.[0].factorEnvNight").value(review.getFactorEnvNight()))
                .andExpect(jsonPath("$.[0].factorEnvOdours").value(review.getFactorEnvOdours()))
                .andExpect(jsonPath("$.[0].factorEnvOther").value(review.getFactorEnvOther()))
                .andExpect(
                        jsonPath("$.[0].factorEnvObstructions")
                                .value(review.getFactorEnvObstructions()))
                .andExpect(jsonPath("$.[0].factorEnvRaining").value(review.getFactorEnvRaining()))
                .andExpect(jsonPath("$.[0].factorEnvSunGlare").value(review.getFactorEnvSunGlare()))
                .andExpect(jsonPath("$.[0].factorEnvSurface").value(review.getFactorEnvSurface()))
                .andExpect(
                        jsonPath("$.[0].factorEnvTemperature")
                                .value(review.getFactorEnvTemperature()))
                .andExpect(
                        jsonPath("$.[0].factorEnvVegetation")
                                .value(review.getFactorEnvVegetation()))
                .andExpect(jsonPath("$.[0].factorEnvHeight").value(review.getFactorEnvHeight()))
                .andExpect(
                        jsonPath("$.[0].factorEnvDescription")
                                .value(review.getFactorEnvDescription()))
                .andExpect(
                        jsonPath("$.[0].factorEnvWhatHappened")
                                .value(review.getFactorEnvWhatHappened()))
                .andExpect(
                        jsonPath("$.[0].factorWorkDescription")
                                .value(review.getFactorWorkDescription()))
                .andExpect(
                        jsonPath("$.[0].factorWorkWhatHappened")
                                .value(review.getFactorWorkWhatHappened()))
                .andExpect(jsonPath("$.[0].riskRating").value(review.getRiskRating()))
                .andExpect(jsonPath("$.[0].consequences").value(review.getConsequences()))
                .andExpect(jsonPath("$.[0].likelihood").value(review.getLikelihood()))
                .andExpect(jsonPath("$.[0].correctiveAction").value(review.getCorrectiveAction()))
                .andExpect(jsonPath("$.[0].dueDate").value("1900-07-08"))
                .andExpect(
                        jsonPath("$.[0].actionsToImplement").value(review.getActionsToImplement()))
                .andExpect(jsonPath("$.[0].allocatedTo").value(review.getAllocatedTo()))
                .andExpect(jsonPath("$.[0].codeBreach").value(review.getCodeBreach().name()))
                .andExpect(
                        jsonPath("$.[0].supportPlanUpdate")
                                .value(review.getSupportPlanUpdate().name()))
                .andExpect(jsonPath("$.[0].howPrevented").value(review.getHowPrevented()))
                .andExpect(jsonPath("$.[0].followUp").value(review.getFollowUp()))
                .andExpect(
                        jsonPath("$.[0].furtherSupport").value(review.getFurtherSupport().name()));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create review")
    void createReview_Success() {
        // Given
        Incident incident = createAnIncident();

        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .incidentId(incident.getId())
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

        // When
        mockMvc.perform(
                        post("/incident-review/create")
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
    @DisplayName("POST /create review")
    void createReview_Success_WithoutFields() {
        // Given
        Incident incident = createAnIncident();

        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .incidentId(incident.getId())
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
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes.name())
                        .supportPlanUpdate(YesNo.Yes.name())
                        .furtherSupport(YesNo.Yes.name())
                        .factorWorkDescription("work")
                        .build();

        // When
        mockMvc.perform(
                        post("/incident-review/create")
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
    @DisplayName("PUT /update review")
    void testUpdateReview_Success() {
        // Given
        Incident incident = createAnIncident();
        IncidentReview review = createAReview(incident);

        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .id(review.getId())
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
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes.name())
                        .furtherSupport(YesNo.Yes.name())
                        .supportPlanUpdate(YesNo.Yes.name())
                        .factorWorkDescription("no work")
                        .build();

        // When
        mockMvc.perform(
                        put("/incident-review/update/" + review.getId())
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
    @DisplayName("PUT /update review failure")
    void testUpdateReview_Failure_ReviewNotPresent() {
        // Given

        IncidentReviewDTO request =
                IncidentReviewDTO.builder()
                        .id(30L)
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

        // When
        mockMvc.perform(
                        put("/incident-review/update/30")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    private IncidentReview createAReview(Incident incident) {
        LocalDate date = LocalDate.of(1900, 7, 8);
        IncidentReview testIncidentReview =
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
                        .dueDate(date)
                        .actionsToImplement("Immediate")
                        .allocatedTo("allocatedTo")
                        .factorWorkDescription("work")
                        .howPrevented("howPrevented")
                        .followUp("followUp")
                        .codeBreach(YesNo.Yes)
                        .furtherSupport(YesNo.Yes)
                        .supportPlanUpdate(YesNo.Yes)
                        .build();
        testIncidentReview = incidentReviewRepository.save(testIncidentReview);
        return testIncidentReview;
    }

    private Incident createAnIncident() {
        String description = "desc";
        String location = "loc";
        String exactLocation = "ex-loc";
        String injuredGivenName = "injured1";
        String injuredFamilyName = "injured2";

        LocalDateTime date = LocalDateTime.of(1900, 7, 8, 10, 23, 33);

        Lookup category =
                Lookup.builder().name("category").lookupType(LookupType.INCIDENT_CATEGORY).build();
        category = lookupRepository.save(category);

        Lookup classification =
                Lookup.builder()
                        .name("classification")
                        .lookupType(LookupType.INCIDENT_CLASSIFICATION)
                        .build();
        classification = lookupRepository.save(classification);

        Lookup type = Lookup.builder().name("type").lookupType(LookupType.INCIDENT_TYPE).build();
        type = lookupRepository.save(type);

        House house = House.builder().houseCode("801").build();
        house = houseRepository.save(house);

        Client client = Client.builder().name("client").build();
        client = clientRepository.save(client);

        User user =
                User.builder()
                        .email("incident@test.com")
                        .password("incident")
                        .firstName("first")
                        .lastName("last")
                        .build();
        user = userRepository.save(user);

        Incident testIncident =
                Incident.builder()
                        .category(category)
                        .classification(classification)
                        .type(type)
                        .client(client)
                        .house(house)
                        .status(Status.RAISED)
                        .dateOccurred(date)
                        .description(description)
                        .location(location)
                        .reportedBy(user)
                        .exactLocation(exactLocation)
                        .injuredGivenName(injuredGivenName)
                        .injuredFamilyName(injuredFamilyName)
                        .build();
        testIncident = incidentRepository.save(testIncident);
        return testIncident;
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
