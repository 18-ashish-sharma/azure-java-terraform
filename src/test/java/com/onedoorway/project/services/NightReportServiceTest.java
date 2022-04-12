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
import com.onedoorway.project.dto.NightReportDTO;
import com.onedoorway.project.dto.NightReportRequest;
import com.onedoorway.project.dto.ParticularNightReportRequest;
import com.onedoorway.project.exception.NightReportServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.NightReport;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.NightReportRepository;
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
class NightReportServiceTest {
    @Mock NightReportRepository mockNightReportRepository;

    @Mock ClientRepository mockClientRepository;

    private NightReportService nightReportService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        nightReportService =
                new NightReportService(mockNightReportRepository, mockClientRepository, context);
    }

    @SneakyThrows
    @Test
    void testCreateNightReport_Success() {

        String cleanToiletBy = "Hazel";
        String cleanBathroomStaffBy = "Anjana";
        String cleanBathroomClientBy = "zen";
        String tidyFurnitureBy = "joe";
        String vacuumFloorsBy = "mary";
        String mopFloorsBy = "jane";
        String washingCycleBy = "Ron";
        String dryingCycleBy = "jim";
        String foldClothesBy = "ruby";
        String putAwayDishesBy = "sima";
        String tidyStaffRoomBy = "rachel";
        String chargeElectronicsBy = "renu";
        String completedDailyReportsBy = "sara";
        String checkMedicationChartBy = "john";
        String fillIncidentReportBy = "Nim";
        String checkFridgeBy = "july";
        String emptyDustbinsBy = "charlotte";
        String takeOutDustbinsBy = "maria";
        String vacuumVehicleBy = "Amitha";
        String checklistVehicleBy = "George";
        String childLockVehicleBy = "Tara";
        LocalDate reportDate = LocalDate.of(2021, 10, 14);
        Instant lastUpdatedAt = context.now();

        NightReportRequest request =
                NightReportRequest.builder()
                        .clientId(1L)
                        .cleanToilet(true)
                        .cleanToiletBy(cleanToiletBy)
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy(cleanBathroomStaffBy)
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy(cleanBathroomClientBy)
                        .tidyFurniture(true)
                        .tidyFurnitureBy(tidyFurnitureBy)
                        .vacuumFloors(true)
                        .vacuumFloorsBy(vacuumFloorsBy)
                        .mopFloors(true)
                        .mopFloorsBy(mopFloorsBy)
                        .washingCycle(true)
                        .washingCycleBy(washingCycleBy)
                        .dryingCycle(true)
                        .dryingCycleBy(dryingCycleBy)
                        .foldClothes(true)
                        .foldClothesBy(foldClothesBy)
                        .putAwayDishes(true)
                        .putAwayDishesBy(putAwayDishesBy)
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy(tidyStaffRoomBy)
                        .chargeElectronics(true)
                        .chargeElectronicsBy(chargeElectronicsBy)
                        .completedDailyReports(true)
                        .completedDailyReportsBy(completedDailyReportsBy)
                        .checkMedicationChart(true)
                        .checkMedicationChartBy(checkMedicationChartBy)
                        .fillIncidentReport(true)
                        .fillIncidentReportBy(fillIncidentReportBy)
                        .checkFridge(true)
                        .checkFridgeBy(checkFridgeBy)
                        .emptyDustbins(true)
                        .emptyDustbinsBy(emptyDustbinsBy)
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy(takeOutDustbinsBy)
                        .vacuumVehicle(true)
                        .vacuumVehicleBy(vacuumVehicleBy)
                        .checklistVehicle(true)
                        .checklistVehicleBy(checklistVehicleBy)
                        .childLockVehicle(true)
                        .childLockVehicleBy(childLockVehicleBy)
                        .reportDate(reportDate)
                        .build();

        Client client =
                Client.builder()
                        .name("name")
                        .gender("gender")
                        .dob(
                                LocalDate.parse(
                                        "1980-09-08", DateTimeFormatter.ofPattern("yyyy-MM-dd")))
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
        nightReportService.createNightReport(request);

        // Then
        NightReport expected =
                NightReport.builder()
                        .id(1L)
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy(cleanToiletBy)
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy(cleanBathroomStaffBy)
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy(cleanBathroomClientBy)
                        .tidyFurniture(true)
                        .tidyFurnitureBy(tidyFurnitureBy)
                        .vacuumFloors(true)
                        .vacuumFloorsBy(vacuumFloorsBy)
                        .mopFloors(true)
                        .mopFloorsBy(mopFloorsBy)
                        .washingCycle(true)
                        .washingCycleBy(washingCycleBy)
                        .dryingCycle(true)
                        .dryingCycleBy(dryingCycleBy)
                        .foldClothes(true)
                        .foldClothesBy(foldClothesBy)
                        .putAwayDishes(true)
                        .putAwayDishesBy(putAwayDishesBy)
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy(tidyStaffRoomBy)
                        .chargeElectronics(true)
                        .chargeElectronicsBy(chargeElectronicsBy)
                        .completedDailyReports(true)
                        .completedDailyReportsBy(completedDailyReportsBy)
                        .checkMedicationChart(true)
                        .checkMedicationChartBy(checkMedicationChartBy)
                        .fillIncidentReport(true)
                        .fillIncidentReportBy(fillIncidentReportBy)
                        .checkFridge(true)
                        .checkFridgeBy(checkFridgeBy)
                        .emptyDustbins(true)
                        .emptyDustbinsBy(emptyDustbinsBy)
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy(takeOutDustbinsBy)
                        .vacuumVehicle(true)
                        .vacuumVehicleBy(vacuumVehicleBy)
                        .checklistVehicle(true)
                        .checklistVehicleBy(checklistVehicleBy)
                        .childLockVehicle(true)
                        .childLockVehicleBy(childLockVehicleBy)
                        .reportDate(reportDate)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();

        ArgumentCaptor<NightReport> nightReportArgumentCaptor =
                ArgumentCaptor.forClass(NightReport.class);
        verify(mockNightReportRepository).save(nightReportArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("cleanToiletBy", equalTo(cleanToiletBy)),
                        HasPropertyWithValue.hasProperty("lastUpdatedAt", equalTo(context.now())),
                        HasPropertyWithValue.hasProperty("reportDate", equalTo(reportDate)),
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("cleanToilet", equalTo(true)),
                        HasPropertyWithValue.hasProperty("cleanToiletBy", equalTo(cleanToiletBy)),
                        HasPropertyWithValue.hasProperty("cleanBathroomStaff", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "cleanBathroomStaffBy", equalTo(cleanBathroomStaffBy)),
                        HasPropertyWithValue.hasProperty("cleanBathroomClient", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "cleanBathroomClientBy", equalTo(cleanBathroomClientBy)),
                        HasPropertyWithValue.hasProperty("tidyFurniture", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "tidyFurnitureBy", equalTo(tidyFurnitureBy)),
                        HasPropertyWithValue.hasProperty("vacuumFloors", equalTo(true)),
                        HasPropertyWithValue.hasProperty("vacuumFloorsBy", equalTo(vacuumFloorsBy)),
                        HasPropertyWithValue.hasProperty("mopFloors", equalTo(true)),
                        HasPropertyWithValue.hasProperty("mopFloorsBy", equalTo(mopFloorsBy)),
                        HasPropertyWithValue.hasProperty("washingCycle", equalTo(true)),
                        HasPropertyWithValue.hasProperty("washingCycleBy", equalTo(washingCycleBy)),
                        HasPropertyWithValue.hasProperty("dryingCycle", equalTo(true)),
                        HasPropertyWithValue.hasProperty("dryingCycleBy", equalTo(dryingCycleBy)),
                        HasPropertyWithValue.hasProperty("foldClothes", equalTo(true)),
                        HasPropertyWithValue.hasProperty("foldClothesBy", equalTo(foldClothesBy)),
                        HasPropertyWithValue.hasProperty("putAwayDishes", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "putAwayDishesBy", equalTo(putAwayDishesBy)),
                        HasPropertyWithValue.hasProperty("tidyStaffRoom", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "tidyStaffRoomBy", equalTo(tidyStaffRoomBy)),
                        HasPropertyWithValue.hasProperty("chargeElectronics", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "chargeElectronicsBy", equalTo(chargeElectronicsBy)),
                        HasPropertyWithValue.hasProperty("completedDailyReports", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "completedDailyReportsBy", equalTo(completedDailyReportsBy)),
                        HasPropertyWithValue.hasProperty("checkMedicationChart", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "checkMedicationChartBy", equalTo(checkMedicationChartBy)),
                        HasPropertyWithValue.hasProperty("fillIncidentReport", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "fillIncidentReportBy", equalTo(fillIncidentReportBy)),
                        HasPropertyWithValue.hasProperty("checkFridge", equalTo(true)),
                        HasPropertyWithValue.hasProperty("checkFridgeBy", equalTo(checkFridgeBy)),
                        HasPropertyWithValue.hasProperty("emptyDustbins", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "emptyDustbinsBy", equalTo(emptyDustbinsBy)),
                        HasPropertyWithValue.hasProperty("takeOutDustbins", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "takeOutDustbinsBy", equalTo(takeOutDustbinsBy)),
                        HasPropertyWithValue.hasProperty("vacuumVehicle", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "vacuumVehicleBy", equalTo(vacuumVehicleBy)),
                        HasPropertyWithValue.hasProperty("checklistVehicle", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "checklistVehicleBy", equalTo(checklistVehicleBy)),
                        HasPropertyWithValue.hasProperty("childLockVehicle", equalTo(true)),
                        HasPropertyWithValue.hasProperty(
                                "childLockVehicleBy", equalTo(childLockVehicleBy)))));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateReport_Success() {
        // Given
        long id = 1L;
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
        client = mockClientRepository.save(client);

        NightReport nightReport =
                NightReport.builder()
                        .id(1L)
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy(cleanToiletBy)
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy(cleanBathroomStaffBy)
                        .cleanBathroomClient(true)
                        .cleanBathroomClientBy(cleanBathroomClientBy)
                        .tidyFurniture(true)
                        .tidyFurnitureBy(tidyFurnitureBy)
                        .vacuumFloors(true)
                        .vacuumFloorsBy(vacuumFloorsBy)
                        .mopFloors(true)
                        .mopFloorsBy(mopFloorsBy)
                        .washingCycle(true)
                        .washingCycleBy(washingCycleBy)
                        .dryingCycle(true)
                        .dryingCycleBy(dryingCycleBy)
                        .foldClothes(true)
                        .foldClothesBy(foldClothesBy)
                        .putAwayDishes(true)
                        .putAwayDishesBy(putAwayDishesBy)
                        .tidyStaffRoom(true)
                        .tidyStaffRoomBy(tidyStaffRoomBy)
                        .chargeElectronics(true)
                        .chargeElectronicsBy(chargeElectronicsBy)
                        .completedDailyReports(true)
                        .completedDailyReportsBy(completedDailyReportsBy)
                        .checkMedicationChart(true)
                        .checkMedicationChartBy(checkMedicationChartBy)
                        .fillIncidentReport(true)
                        .fillIncidentReportBy(fillIncidentReportBy)
                        .checkFridge(true)
                        .checkFridgeBy(checkFridgeBy)
                        .emptyDustbins(true)
                        .emptyDustbinsBy(emptyDustbinsBy)
                        .takeOutDustbins(true)
                        .takeOutDustbinsBy(takeOutDustbinsBy)
                        .vacuumVehicle(true)
                        .vacuumVehicleBy(vacuumVehicleBy)
                        .checklistVehicle(true)
                        .checklistVehicleBy(checklistVehicleBy)
                        .childLockVehicle(true)
                        .childLockVehicleBy(childLockVehicleBy)
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(context.now())
                        .build();

        NightReportRequest request =
                NightReportRequest.builder()
                        .id(id)
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

        when(mockNightReportRepository.findById(id)).thenReturn(Optional.of(nightReport));

        // when
        nightReportService.updateReport(id, request);

        NightReport expected =
                NightReport.builder()
                        .id(id)
                        .client(client)
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
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(nightReport.getLastUpdatedAt())
                        .build();

        // then
        verify(mockNightReportRepository).save(eq(expected));
    }

    @Test
    void testUpdateReport_Failure_ReportNotFound() {
        // Given
        NightReportRequest request =
                NightReportRequest.builder()
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
                        .build();

        assertThrows(
                NightReportServiceException.class,
                () -> {
                    // When
                    nightReportService.updateReport(1L, request);
                });
    }

    @SneakyThrows
    @Test
    void testGetNightReport_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();

        NightReport nightReport =
                NightReport.builder()
                        .id(1L)
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy("Hazel")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("mary")
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
                        .reportDate(LocalDate.now())
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockNightReportRepository.findByClient_IdAndReportDate(1L, LocalDate.now()))
                .thenReturn(nightReport);
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        NightReportDTO expected = new ModelMapper().map(nightReport, NightReportDTO.class);

        // When
        NightReportDTO actual = nightReportService.getNightReportById(1L);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testGetNightReport_Failure_ReportNotFound() {
        // Given
        long id = 1;

        assertThrows(
                NightReportServiceException.class,
                () -> {
                    // When
                    nightReportService.getNightReportById(id);
                });
    }

    @SneakyThrows
    @Test
    void testGetParticularNightReport_Success() {
        // Given
        String date = "2021-09-09";
        Client client = Client.builder().id(1L).name("client").build();

        NightReport nightReport =
                NightReport.builder()
                        .id(1L)
                        .client(client)
                        .cleanToilet(true)
                        .cleanToiletBy("Hazel")
                        .cleanBathroomStaff(true)
                        .cleanBathroomStaffBy("mary")
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
                        .reportDate(LocalDate.parse(date))
                        .lastUpdatedAt(context.now())
                        .build();
        ParticularNightReportRequest request =
                ParticularNightReportRequest.builder()
                        .clientId(client.getId())
                        .reportDate(date)
                        .build();

        when(mockNightReportRepository.findByClient_IdAndReportDate(1L, LocalDate.parse(date)))
                .thenReturn(nightReport);
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        NightReportDTO expected = new ModelMapper().map(nightReport, NightReportDTO.class);

        // When
        NightReportDTO actual = nightReportService.getParticularNightReport(request);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetParticularNightReport_Failure_ClientNotFound() {
        // Given
        long id = 2;
        ParticularNightReportRequest request =
                ParticularNightReportRequest.builder()
                        .clientId(id)
                        .reportDate("2021-10-09")
                        .build();

        assertThrows(
                NightReportServiceException.class,
                () -> {
                    // When
                    nightReportService.getParticularNightReport(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetParticularNightReport_Failure_NightReportNotFound() {
        // Given
        long id = 1;
        Client client = Client.builder().name("clientName").id(id).build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        ParticularNightReportRequest request =
                ParticularNightReportRequest.builder()
                        .clientId(id)
                        .reportDate("2021-10-09")
                        .build();

        assertThrows(
                NightReportServiceException.class,
                () -> {
                    // When
                    nightReportService.getParticularNightReport(request);
                });
    }
}
