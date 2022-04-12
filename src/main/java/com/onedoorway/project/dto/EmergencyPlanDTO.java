package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyPlanDTO {
    private String houseCode;
    private String emergencyPlanUrl;
    private String emergencyHandoutUrl;
}
