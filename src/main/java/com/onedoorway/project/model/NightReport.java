package com.onedoorway.project.model;

import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "night_report",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "report_date"})})
public class NightReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "clean_toilet")
    private Boolean cleanToilet;

    @Column(name = "clean_toiletBy")
    private String cleanToiletBy;

    @Column(name = "clean_BathroomStaff")
    private Boolean cleanBathroomStaff;

    @Column(name = "clean_BathroomStaffBy")
    private String cleanBathroomStaffBy;

    @Column(name = "clean_BathroomClient")
    private Boolean cleanBathroomClient;

    @Column(name = "clean_BathroomClientBy")
    private String cleanBathroomClientBy;

    @Column(name = "tidy_Furniture")
    private Boolean tidyFurniture;

    @Column(name = "tidy_FurnitureBy")
    private String tidyFurnitureBy;

    @Column(name = "vacuum_Floors")
    private Boolean vacuumFloors;

    @Column(name = "vacuum_FloorsBy")
    private String vacuumFloorsBy;

    @Column(name = "mop_Floors")
    private Boolean mopFloors;

    @Column(name = "mop_FloorsBy")
    private String mopFloorsBy;

    @Column(name = "washing_Cycle")
    private Boolean washingCycle;

    @Column(name = "washing_CycleBy")
    private String washingCycleBy;

    @Column(name = "drying_Cycle")
    private Boolean dryingCycle;

    @Column(name = "drying_CycleBy")
    private String dryingCycleBy;

    @Column(name = "fold_Clothes")
    private Boolean foldClothes;

    @Column(name = "fold_ClothesBy")
    private String foldClothesBy;

    @Column(name = "put_AwayDishes")
    private Boolean putAwayDishes;

    @Column(name = "put_AwayDishesBy")
    private String putAwayDishesBy;

    @Column(name = "tidy_StaffRoom")
    private Boolean tidyStaffRoom;

    @Column(name = "tidy_StaffRoomBy")
    private String tidyStaffRoomBy;

    @Column(name = "charge_Electronics")
    private Boolean chargeElectronics;

    @Column(name = "charge_ElectronicsBy")
    private String chargeElectronicsBy;

    @Column(name = "completed_DailyReports")
    private Boolean completedDailyReports;

    @Column(name = "completed_DailyReportsBy")
    private String completedDailyReportsBy;

    @Column(name = "check_MedicationChart")
    private Boolean checkMedicationChart;

    @Column(name = "check_MedicationChartBy")
    private String checkMedicationChartBy;

    @Column(name = "fill_IncidentReport")
    private Boolean fillIncidentReport;

    @Column(name = "fill_IncidentReportBy")
    private String fillIncidentReportBy;

    @Column(name = "check_Fridge")
    private Boolean checkFridge;

    @Column(name = "check_FridgeBy")
    private String checkFridgeBy;

    @Column(name = "empty_Dustbins")
    private Boolean emptyDustbins;

    @Column(name = "empty_DustbinsBy")
    private String emptyDustbinsBy;

    @Column(name = "takeOut_Dustbins")
    private Boolean takeOutDustbins;

    @Column(name = "takeOut_DustbinsBy")
    private String takeOutDustbinsBy;

    @Column(name = "vacuum_Vehicle")
    private Boolean vacuumVehicle;

    @Column(name = "vacuum_VehicleBy")
    private String vacuumVehicleBy;

    @Column(name = "checklist_Vehicle")
    private Boolean checklistVehicle;

    @Column(name = "checklist_VehicleBy")
    private String checklistVehicleBy;

    @Column(name = "childLock_Vehicle")
    private Boolean childLockVehicle;

    @Column(name = "childLock_VehicleBy")
    private String childLockVehicleBy;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = " last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
