package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = NightReportRequest.NightReportRequestBuilder.class)
public class NightReportRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class NightReportRequestBuilder {}

    private Long id;
    @NotNull private long clientId;
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
    private LocalDate reportDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant currentLastUpdatedAt;
}
