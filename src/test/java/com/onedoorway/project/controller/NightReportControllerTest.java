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
import com.onedoorway.project.dto.NightReportRequest;
import com.onedoorway.project.dto.ParticularNightReportRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.NightReportRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
class NightReportControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private NightReportRepository nightReportRepository;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().id(1).name("USER").build())))
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
        nightReportRepository.deleteAll();
        userRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create nightReport")
    void testCreateNightReportSuccess() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        client = clientRepository.save(client);

        NightReportRequest request =
                NightReportRequest.builder()
                        .clientId(client.getId())
                        .cleanToilet(true)
                        .cleanToiletBy("Hazel")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("Anjana")
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy("zen")
                        .tidyFurniture(true)
                        .tidyFurnitureBy("joe")
                        .vacuumFloors(true)
                        .vacuumFloorsBy("mary")
                        .mopFloors(true)
                        .mopFloorsBy("jane")
                        .washingCycle(true)
                        .washingCycleBy("Ron")
                        .dryingCycle(true)
                        .dryingCycleBy("jim")
                        .foldClothes(true)
                        .foldClothesBy("ruby")
                        .putAwayDishes(true)
                        .putAwayDishesBy("sima")
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy("rachel")
                        .chargeElectronics(true)
                        .chargeElectronicsBy("renu")
                        .completedDailyReports(true)
                        .completedDailyReportsBy("sara")
                        .checkMedicationChart(true)
                        .checkMedicationChartBy("john")
                        .fillIncidentReport(true)
                        .fillIncidentReportBy("Nim")
                        .checkFridge(true)
                        .checkFridgeBy("july")
                        .emptyDustbins(true)
                        .emptyDustbinsBy("charlotte")
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy("maria")
                        .vacuumVehicle(true)
                        .vacuumVehicleBy("Amitha")
                        .checklistVehicle(true)
                        .checklistVehicleBy("George")
                        .childLockVehicle(true)
                        .childLockVehicleBy("Tara")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .build();

        // When
        mockMvc.perform(
                        post("/night-report/create")
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
    @DisplayName("POST /create nightReport failure")
    void createNightReport_Success_WithoutFields() {
        // Given
        Client client = Client.builder().id(2L).name("client").build();
        client = clientRepository.save(client);

        NightReportRequest request =
                NightReportRequest.builder()
                        .clientId(client.getId())
                        .cleanToilet(true)
                        .cleanToiletBy("Hazel")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("Anjana")
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy("zen")
                        .tidyFurniture(true)
                        .tidyFurnitureBy("joe")
                        .vacuumFloors(true)
                        .vacuumFloorsBy("mary")
                        .mopFloors(true)
                        .foldClothesBy("ruby")
                        .putAwayDishes(true)
                        .putAwayDishesBy("sima")
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy("rachel")
                        .chargeElectronics(true)
                        .chargeElectronicsBy("renu")
                        .completedDailyReports(true)
                        .completedDailyReportsBy("sara")
                        .checkMedicationChart(true)
                        .checkMedicationChartBy("john")
                        .fillIncidentReport(true)
                        .fillIncidentReportBy("Nim")
                        .checkFridge(true)
                        .checkFridgeBy("july")
                        .emptyDustbins(true)
                        .emptyDustbinsBy("charlotte")
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy("maria")
                        .vacuumVehicle(true)
                        .vacuumVehicleBy("Amitha")
                        .checklistVehicle(true)
                        .checklistVehicleBy("George")
                        .childLockVehicle(true)
                        .childLockVehicleBy("Tara")
                        .reportDate(LocalDate.of(2021, 10, 14))
                        .build();

        // When
        mockMvc.perform(
                        post("/night-report/create")
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
    @DisplayName("GET / get nightReport")
    void testGetNightReport_Success() {
        // Given
        long id = 1;
        LocalDate reportDate = LocalDate.now();
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        NightReport nightReport =
                NightReport.builder()
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy("Hazel")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("Anjana")
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy("zen")
                        .tidyFurniture(true)
                        .tidyFurnitureBy("joe")
                        .vacuumFloors(true)
                        .vacuumFloorsBy("mary")
                        .mopFloors(true)
                        .mopFloorsBy("jane")
                        .washingCycle(true)
                        .washingCycleBy("Ron")
                        .dryingCycle(true)
                        .dryingCycleBy("jim")
                        .foldClothes(true)
                        .foldClothesBy("Ruby")
                        .putAwayDishes(true)
                        .putAwayDishesBy("sima")
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy("rachel")
                        .chargeElectronics(true)
                        .chargeElectronicsBy("renu")
                        .completedDailyReports(true)
                        .completedDailyReportsBy("sara")
                        .checkMedicationChart(true)
                        .checkMedicationChartBy("john")
                        .fillIncidentReport(true)
                        .fillIncidentReportBy("Nim")
                        .checkFridge(true)
                        .checkFridgeBy("july")
                        .emptyDustbins(true)
                        .emptyDustbinsBy("charlotte")
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy("Maria")
                        .vacuumVehicle(true)
                        .vacuumVehicleBy("Amitha")
                        .checklistVehicle(true)
                        .checklistVehicleBy("George")
                        .childLockVehicle(true)
                        .childLockVehicleBy("Tara")
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        nightReportRepository.save(nightReport);

        // When
        mockMvc.perform(
                        get("/night-report/get/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(nightReport.getId()))
                .andExpect(jsonPath("$.clientId").value(nightReport.getClient().getId()))
                .andExpect(jsonPath("$.clientName").value(nightReport.getClient().getName()))
                .andExpect(jsonPath("$.cleanToilet").value(nightReport.getCleanToilet()))
                .andExpect(jsonPath("$.cleanToiletBy").value(nightReport.getCleanToiletBy()))
                .andExpect(
                        jsonPath("$.cleanBathroomStaff").value(nightReport.getCleanBathroomStaff()))
                .andExpect(
                        jsonPath("$.cleanBathroomStaffBy")
                                .value(nightReport.getCleanBathroomStaffBy()))
                .andExpect(
                        jsonPath("$.cleanBathroomClient")
                                .value(nightReport.getCleanBathroomClient()))
                .andExpect(
                        jsonPath("$.cleanBathroomClientBy")
                                .value(nightReport.getCleanBathroomClientBy()))
                .andExpect(jsonPath("$.tidyFurniture").value(nightReport.getTidyFurniture()))
                .andExpect(jsonPath("$.tidyFurnitureBy").value(nightReport.getTidyFurnitureBy()))
                .andExpect(jsonPath("$.vacuumFloors").value(nightReport.getVacuumFloors()))
                .andExpect(jsonPath("$.vacuumFloorsBy").value(nightReport.getVacuumFloorsBy()))
                .andExpect(jsonPath("$.mopFloors").value(nightReport.getMopFloors()))
                .andExpect(jsonPath("$.mopFloorsBy").value(nightReport.getMopFloorsBy()))
                .andExpect(jsonPath("$.washingCycle").value(nightReport.getWashingCycle()))
                .andExpect(jsonPath("$.washingCycleBy").value(nightReport.getWashingCycleBy()))
                .andExpect(jsonPath("$.dryingCycle").value(nightReport.getDryingCycle()))
                .andExpect(jsonPath("$.dryingCycleBy").value(nightReport.getDryingCycleBy()))
                .andExpect(jsonPath("$.foldClothes").value(nightReport.getFoldClothes()))
                .andExpect(jsonPath("$.foldClothesBy").value(nightReport.getFoldClothesBy()))
                .andExpect(jsonPath("$.putAwayDishes").value(nightReport.getPutAwayDishes()))
                .andExpect(jsonPath("$.putAwayDishesBy").value(nightReport.getPutAwayDishesBy()))
                .andExpect(jsonPath("$.tidyStaffRoom").value(nightReport.getTidyStaffRoom()))
                .andExpect(jsonPath("$.tidyStaffRoomBy").value(nightReport.getTidyStaffRoomBy()))
                .andExpect(
                        jsonPath("$.chargeElectronics").value(nightReport.getChargeElectronics()))
                .andExpect(
                        jsonPath("$.chargeElectronicsBy")
                                .value(nightReport.getChargeElectronicsBy()))
                .andExpect(
                        jsonPath("$.completedDailyReports")
                                .value(nightReport.getCompletedDailyReports()))
                .andExpect(
                        jsonPath("$.completedDailyReportsBy")
                                .value(nightReport.getCompletedDailyReportsBy()))
                .andExpect(
                        jsonPath("$.checkMedicationChart")
                                .value(nightReport.getCheckMedicationChart()))
                .andExpect(
                        jsonPath("$.checkMedicationChartBy")
                                .value(nightReport.getCheckMedicationChartBy()))
                .andExpect(
                        jsonPath("$.fillIncidentReport").value(nightReport.getFillIncidentReport()))
                .andExpect(
                        jsonPath("$.fillIncidentReportBy")
                                .value(nightReport.getFillIncidentReportBy()))
                .andExpect(jsonPath("$.checkFridge").value(nightReport.getCheckFridge()))
                .andExpect(jsonPath("$.checkFridgeBy").value(nightReport.getCheckFridgeBy()))
                .andExpect(jsonPath("$.emptyDustbins").value(nightReport.getEmptyDustbins()))
                .andExpect(jsonPath("$.emptyDustbinsBy").value(nightReport.getEmptyDustbinsBy()))
                .andExpect(jsonPath("$.takeOutDustbins").value(nightReport.getTakeOutDustbins()))
                .andExpect(
                        jsonPath("$.takeOutDustbinsBy").value(nightReport.getTakeOutDustbinsBy()))
                .andExpect(jsonPath("$.vacuumVehicle").value(nightReport.getVacuumVehicle()))
                .andExpect(jsonPath("$.vacuumVehicleBy").value(nightReport.getVacuumVehicleBy()))
                .andExpect(jsonPath("$.checklistVehicle").value(nightReport.getChecklistVehicle()))
                .andExpect(
                        jsonPath("$.checklistVehicleBy").value(nightReport.getChecklistVehicleBy()))
                .andExpect(jsonPath("$.childLockVehicle").value(nightReport.getChildLockVehicle()))
                .andExpect(
                        jsonPath("$.childLockVehicleBy").value(nightReport.getChildLockVehicleBy()))
                .andExpect(
                        jsonPath("$.reportDate")
                                .value(
                                        reportDate.format(
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update night reports success")
    void testUpdateReport_Success() {
        // Given
        String cleanToiletBy = "staff 1";
        String cleanBathroomStaffBy = "staff 2";
        String cleanBathroomClientBy = "staff 3";
        String tidyFurnitureBy = "staff 4";
        String vacuumFloorsBy = "staff 5";
        String mopFloorsBy = "staff 6";
        String washingCycleBy = "staff 7";
        String dryingCycleBy = "staff 8";
        String foldClothesBy = "staff 9";
        String putAwayDishesBy = "staff 10";
        String tidyStaffRoomBy = "staff 11";
        String chargeElectronicsBy = "staff 12";
        String completedDailyReportsBy = "staff 13";
        String checkMedicationChartBy = "staff 14";
        String fillIncidentReportBy = "staff 15";
        String checkFridgeBy = "staff 16";
        String emptyDustbinsBy = "staff 17";
        String takeOutDustbinsBy = "staff 18";
        String vacuumVehicleBy = "staff 19";
        String checklistVehicleBy = "staff 20";
        String childLockVehicleBy = "staff 21";

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        NightReport nightReport =
                NightReport.builder()
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy("staff A")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("staff B")
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy("staff C")
                        .tidyFurniture(true)
                        .tidyFurnitureBy("staff D")
                        .vacuumFloors(true)
                        .vacuumFloorsBy("staff E")
                        .mopFloors(true)
                        .mopFloorsBy("staff F")
                        .washingCycle(true)
                        .washingCycleBy("staff G")
                        .dryingCycle(true)
                        .dryingCycleBy("staff H")
                        .foldClothes(true)
                        .foldClothesBy("staff I")
                        .putAwayDishes(true)
                        .putAwayDishesBy("staff J")
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy("staff K")
                        .chargeElectronics(true)
                        .chargeElectronicsBy("staff L")
                        .completedDailyReports(true)
                        .completedDailyReportsBy("staff L")
                        .checkMedicationChart(true)
                        .checkMedicationChartBy("staff M")
                        .fillIncidentReport(true)
                        .fillIncidentReportBy("staff N")
                        .checkFridge(true)
                        .checkFridgeBy("staff O")
                        .emptyDustbins(true)
                        .emptyDustbinsBy("staff P")
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy("staff Q")
                        .vacuumVehicle(true)
                        .vacuumVehicleBy("staff R")
                        .checklistVehicle(true)
                        .checklistVehicleBy("staff S")
                        .childLockVehicle(true)
                        .childLockVehicleBy("staff T")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(Instant.now())
                        .build();
        nightReport = nightReportRepository.save(nightReport);

        NightReportRequest request =
                NightReportRequest.builder()
                        .id(nightReport.getId())
                        .cleanToilet(false)
                        .cleanToiletBy(cleanToiletBy)
                        .cleanBathroomStaff(false)
                        .cleanBathroomStaffBy(cleanBathroomStaffBy)
                        .cleanBathroomClient(false)
                        .cleanBathroomClientBy(cleanBathroomClientBy)
                        .tidyFurniture(false)
                        .tidyFurnitureBy(tidyFurnitureBy)
                        .vacuumFloors(false)
                        .vacuumFloorsBy(vacuumFloorsBy)
                        .mopFloors(false)
                        .mopFloorsBy(mopFloorsBy)
                        .washingCycle(false)
                        .washingCycleBy(washingCycleBy)
                        .dryingCycle(false)
                        .dryingCycleBy(dryingCycleBy)
                        .foldClothes(false)
                        .foldClothesBy(foldClothesBy)
                        .putAwayDishes(false)
                        .putAwayDishesBy(putAwayDishesBy)
                        .tidyStaffRoom(false)
                        .tidyStaffRoomBy(tidyStaffRoomBy)
                        .chargeElectronics(false)
                        .chargeElectronicsBy(chargeElectronicsBy)
                        .completedDailyReports(false)
                        .completedDailyReportsBy(completedDailyReportsBy)
                        .checkMedicationChart(false)
                        .checkMedicationChartBy(checkMedicationChartBy)
                        .fillIncidentReport(false)
                        .fillIncidentReportBy(fillIncidentReportBy)
                        .checkFridge(false)
                        .checkFridgeBy(checkFridgeBy)
                        .emptyDustbins(false)
                        .emptyDustbinsBy(emptyDustbinsBy)
                        .takeOutDustbins(false)
                        .takeOutDustbinsBy(takeOutDustbinsBy)
                        .vacuumVehicle(false)
                        .vacuumVehicleBy(vacuumVehicleBy)
                        .checklistVehicle(false)
                        .checklistVehicleBy(checklistVehicleBy)
                        .childLockVehicle(false)
                        .childLockVehicleBy(childLockVehicleBy)
                        .currentLastUpdatedAt(
                                nightReport.getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS))
                        .build();

        // When
        mockMvc.perform(
                        put("/night-report/update/{id}", request.getId())
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
    @DisplayName("PUT /update night reports failure")
    void testUpdateReport_Failure_ReportNotFound() {
        // Given
        String cleanToiletBy = "staff 1";
        String cleanBathroomStaffBy = "staff 2";
        String cleanBathroomClientBy = "staff 3";
        String tidyFurnitureBy = "staff 4";
        String vacuumFloorsBy = "staff 5";
        String mopFloorsBy = "staff 6";
        String washingCycleBy = "staff 7";
        String dryingCycleBy = "staff 8";
        String foldClothesBy = "staff 9";
        String putAwayDishesBy = "staff 10";
        String tidyStaffRoomBy = "staff 11";
        String chargeElectronicsBy = "staff 12";
        String completedDailyReportsBy = "staff 13";
        String checkMedicationChartBy = "staff 14";
        String fillIncidentReportBy = "staff 15";
        String checkFridgeBy = "staff 16";
        String emptyDustbinsBy = "staff 17";
        String takeOutDustbinsBy = "staff 18";
        String vacuumVehicleBy = "staff 19";
        String checklistVehicleBy = "staff 20";
        String childLockVehicleBy = "staff 21";

        Client client = Client.builder().id(1).name("client").build();
        client = clientRepository.save(client);

        NightReport nightReport =
                NightReport.builder()
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy("staff A")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("staff B")
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy("staff C")
                        .tidyFurniture(true)
                        .tidyFurnitureBy("staff D")
                        .vacuumFloors(true)
                        .vacuumFloorsBy("staff E")
                        .mopFloors(true)
                        .mopFloorsBy("staff F")
                        .washingCycle(true)
                        .washingCycleBy("staff G")
                        .dryingCycle(true)
                        .dryingCycleBy("staff H")
                        .foldClothes(true)
                        .foldClothesBy("staff I")
                        .putAwayDishes(true)
                        .putAwayDishesBy("staff J")
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy("staff K")
                        .chargeElectronics(true)
                        .chargeElectronicsBy("staff L")
                        .completedDailyReports(true)
                        .completedDailyReportsBy("staff L")
                        .checkMedicationChart(true)
                        .checkMedicationChartBy("staff M")
                        .fillIncidentReport(true)
                        .fillIncidentReportBy("staff N")
                        .checkFridge(true)
                        .checkFridgeBy("staff O")
                        .emptyDustbins(true)
                        .emptyDustbinsBy("staff P")
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy("staff Q")
                        .vacuumVehicle(true)
                        .vacuumVehicleBy("staff R")
                        .checklistVehicle(true)
                        .checklistVehicleBy("staff S")
                        .childLockVehicle(true)
                        .childLockVehicleBy("staff T")
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(Instant.now())
                        .build();
        nightReport = nightReportRepository.save(nightReport);

        NightReportRequest request =
                NightReportRequest.builder()
                        .id(nightReport.getId())
                        .cleanToilet(false)
                        .cleanToiletBy(cleanToiletBy)
                        .cleanBathroomStaff(false)
                        .cleanBathroomStaffBy(cleanBathroomStaffBy)
                        .cleanBathroomClient(false)
                        .cleanBathroomClientBy(cleanBathroomClientBy)
                        .tidyFurniture(false)
                        .tidyFurnitureBy(tidyFurnitureBy)
                        .vacuumFloors(false)
                        .vacuumFloorsBy(vacuumFloorsBy)
                        .mopFloors(false)
                        .mopFloorsBy(mopFloorsBy)
                        .washingCycle(false)
                        .washingCycleBy(washingCycleBy)
                        .dryingCycle(false)
                        .dryingCycleBy(dryingCycleBy)
                        .foldClothes(false)
                        .foldClothesBy(foldClothesBy)
                        .putAwayDishes(false)
                        .putAwayDishesBy(putAwayDishesBy)
                        .tidyStaffRoom(false)
                        .tidyStaffRoomBy(tidyStaffRoomBy)
                        .chargeElectronics(false)
                        .chargeElectronicsBy(chargeElectronicsBy)
                        .completedDailyReports(false)
                        .completedDailyReportsBy(completedDailyReportsBy)
                        .checkMedicationChart(false)
                        .checkMedicationChartBy(checkMedicationChartBy)
                        .fillIncidentReport(false)
                        .fillIncidentReportBy(fillIncidentReportBy)
                        .checkFridge(false)
                        .checkFridgeBy(checkFridgeBy)
                        .emptyDustbins(false)
                        .emptyDustbinsBy(emptyDustbinsBy)
                        .takeOutDustbins(false)
                        .takeOutDustbinsBy(takeOutDustbinsBy)
                        .vacuumVehicle(false)
                        .vacuumVehicleBy(vacuumVehicleBy)
                        .checklistVehicle(false)
                        .checklistVehicleBy(checklistVehicleBy)
                        .childLockVehicle(false)
                        .childLockVehicleBy(childLockVehicleBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/night-report/update/{id}", 500)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get particular nightReport")
    void testGetParticularNightReport_Success() {
        // Given
        long id = 1;
        String date = "2021-09-09";
        LocalDate reportDate = LocalDate.parse(date);
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        NightReport nightReport =
                NightReport.builder()
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy("Hazel")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("Anjana")
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy("zen")
                        .tidyFurniture(true)
                        .tidyFurnitureBy("joe")
                        .vacuumFloors(true)
                        .vacuumFloorsBy("mary")
                        .mopFloors(true)
                        .mopFloorsBy("jane")
                        .washingCycle(true)
                        .washingCycleBy("Ron")
                        .dryingCycle(true)
                        .dryingCycleBy("jim")
                        .foldClothes(true)
                        .foldClothesBy("Ruby")
                        .putAwayDishes(true)
                        .putAwayDishesBy("sima")
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy("rachel")
                        .chargeElectronics(true)
                        .chargeElectronicsBy("renu")
                        .completedDailyReports(true)
                        .completedDailyReportsBy("sara")
                        .checkMedicationChart(true)
                        .checkMedicationChartBy("john")
                        .fillIncidentReport(true)
                        .fillIncidentReportBy("Nim")
                        .checkFridge(true)
                        .checkFridgeBy("july")
                        .emptyDustbins(true)
                        .emptyDustbinsBy("charlotte")
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy("Maria")
                        .vacuumVehicle(true)
                        .vacuumVehicleBy("Amitha")
                        .checklistVehicle(true)
                        .checklistVehicleBy("George")
                        .childLockVehicle(true)
                        .childLockVehicleBy("Tara")
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        nightReportRepository.save(nightReport);

        ParticularNightReportRequest request =
                ParticularNightReportRequest.builder()
                        .clientId(client.getId())
                        .reportDate(date)
                        .build();

        // When
        mockMvc.perform(
                        post("/night-report/get-particular")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(nightReport.getId()))
                .andExpect(jsonPath("$.clientId").value(nightReport.getClient().getId()))
                .andExpect(jsonPath("$.clientName").value(nightReport.getClient().getName()))
                .andExpect(jsonPath("$.cleanToilet").value(nightReport.getCleanToilet()))
                .andExpect(jsonPath("$.cleanToiletBy").value(nightReport.getCleanToiletBy()))
                .andExpect(
                        jsonPath("$.cleanBathroomStaff").value(nightReport.getCleanBathroomStaff()))
                .andExpect(
                        jsonPath("$.cleanBathroomStaffBy")
                                .value(nightReport.getCleanBathroomStaffBy()))
                .andExpect(
                        jsonPath("$.cleanBathroomClient")
                                .value(nightReport.getCleanBathroomClient()))
                .andExpect(
                        jsonPath("$.cleanBathroomClientBy")
                                .value(nightReport.getCleanBathroomClientBy()))
                .andExpect(jsonPath("$.tidyFurniture").value(nightReport.getTidyFurniture()))
                .andExpect(jsonPath("$.tidyFurnitureBy").value(nightReport.getTidyFurnitureBy()))
                .andExpect(jsonPath("$.vacuumFloors").value(nightReport.getVacuumFloors()))
                .andExpect(jsonPath("$.vacuumFloorsBy").value(nightReport.getVacuumFloorsBy()))
                .andExpect(jsonPath("$.mopFloors").value(nightReport.getMopFloors()))
                .andExpect(jsonPath("$.mopFloorsBy").value(nightReport.getMopFloorsBy()))
                .andExpect(jsonPath("$.washingCycle").value(nightReport.getWashingCycle()))
                .andExpect(jsonPath("$.washingCycleBy").value(nightReport.getWashingCycleBy()))
                .andExpect(jsonPath("$.dryingCycle").value(nightReport.getDryingCycle()))
                .andExpect(jsonPath("$.dryingCycleBy").value(nightReport.getDryingCycleBy()))
                .andExpect(jsonPath("$.foldClothes").value(nightReport.getFoldClothes()))
                .andExpect(jsonPath("$.foldClothesBy").value(nightReport.getFoldClothesBy()))
                .andExpect(jsonPath("$.putAwayDishes").value(nightReport.getPutAwayDishes()))
                .andExpect(jsonPath("$.putAwayDishesBy").value(nightReport.getPutAwayDishesBy()))
                .andExpect(jsonPath("$.tidyStaffRoom").value(nightReport.getTidyStaffRoom()))
                .andExpect(jsonPath("$.tidyStaffRoomBy").value(nightReport.getTidyStaffRoomBy()))
                .andExpect(
                        jsonPath("$.chargeElectronics").value(nightReport.getChargeElectronics()))
                .andExpect(
                        jsonPath("$.chargeElectronicsBy")
                                .value(nightReport.getChargeElectronicsBy()))
                .andExpect(
                        jsonPath("$.completedDailyReports")
                                .value(nightReport.getCompletedDailyReports()))
                .andExpect(
                        jsonPath("$.completedDailyReportsBy")
                                .value(nightReport.getCompletedDailyReportsBy()))
                .andExpect(
                        jsonPath("$.checkMedicationChart")
                                .value(nightReport.getCheckMedicationChart()))
                .andExpect(
                        jsonPath("$.checkMedicationChartBy")
                                .value(nightReport.getCheckMedicationChartBy()))
                .andExpect(
                        jsonPath("$.fillIncidentReport").value(nightReport.getFillIncidentReport()))
                .andExpect(
                        jsonPath("$.fillIncidentReportBy")
                                .value(nightReport.getFillIncidentReportBy()))
                .andExpect(jsonPath("$.checkFridge").value(nightReport.getCheckFridge()))
                .andExpect(jsonPath("$.checkFridgeBy").value(nightReport.getCheckFridgeBy()))
                .andExpect(jsonPath("$.emptyDustbins").value(nightReport.getEmptyDustbins()))
                .andExpect(jsonPath("$.emptyDustbinsBy").value(nightReport.getEmptyDustbinsBy()))
                .andExpect(jsonPath("$.takeOutDustbins").value(nightReport.getTakeOutDustbins()))
                .andExpect(
                        jsonPath("$.takeOutDustbinsBy").value(nightReport.getTakeOutDustbinsBy()))
                .andExpect(jsonPath("$.vacuumVehicle").value(nightReport.getVacuumVehicle()))
                .andExpect(jsonPath("$.vacuumVehicleBy").value(nightReport.getVacuumVehicleBy()))
                .andExpect(jsonPath("$.checklistVehicle").value(nightReport.getChecklistVehicle()))
                .andExpect(
                        jsonPath("$.checklistVehicleBy").value(nightReport.getChecklistVehicleBy()))
                .andExpect(jsonPath("$.childLockVehicle").value(nightReport.getChildLockVehicle()))
                .andExpect(
                        jsonPath("$.childLockVehicleBy").value(nightReport.getChildLockVehicleBy()))
                .andExpect(
                        jsonPath("$.reportDate")
                                .value(
                                        reportDate.format(
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(
                        jsonPath("$.lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get particular nightReport failure")
    void testGetParticularNightReport_Failure() {
        // Given
        long id = 20;
        String date = "2021-07-09";
        ParticularNightReportRequest request =
                ParticularNightReportRequest.builder().clientId(id).reportDate(date).build();

        // When
        mockMvc.perform(
                        post("/night-report/get-particular")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
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
