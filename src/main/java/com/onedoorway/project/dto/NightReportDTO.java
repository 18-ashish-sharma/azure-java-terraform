package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NightReportDTO {
    private long reportId;
    private String clientName;
    private long clientId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    private Boolean cleanToilet;
    private String cleanToiletBy;
    private Boolean cleanBathroomStaff;
    private String cleanBathroomStaffBy;
    private Boolean cleanBathroomClient;
    private String cleanBathroomClientBy;
    private Boolean tidyFurniture;
    private String tidyFurnitureBy;
    private Boolean vacuumFloors;
    private String vacuumFloorsBy;
    private Boolean mopFloors;
    private String mopFloorsBy;
    private Boolean washingCycle;
    private String washingCycleBy;
    private Boolean dryingCycle;
    private String dryingCycleBy;
    private Boolean foldClothes;
    private String foldClothesBy;
    private Boolean putAwayDishes;
    private String putAwayDishesBy;
    private Boolean tidyStaffRoom;
    private String tidyStaffRoomBy;
    private Boolean chargeElectronics;
    private String chargeElectronicsBy;
    private Boolean completedDailyReports;
    private String completedDailyReportsBy;
    private Boolean checkMedicationChart;
    private String checkMedicationChartBy;
    private Boolean fillIncidentReport;
    private String fillIncidentReportBy;
    private Boolean checkFridge;
    private String checkFridgeBy;
    private Boolean emptyDustbins;
    private String emptyDustbinsBy;
    private Boolean takeOutDustbins;
    private String takeOutDustbinsBy;
    private Boolean vacuumVehicle;
    private String vacuumVehicleBy;
    private Boolean checklistVehicle;
    private String checklistVehicleBy;
    private Boolean childLockVehicle;
    private String childLockVehicleBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
