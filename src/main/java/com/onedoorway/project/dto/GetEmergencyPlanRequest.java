package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = GetEmergencyPlanRequest.GetEmergencyPlanRequestBuilder.class)
public class GetEmergencyPlanRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class GetEmergencyPlanRequestBuilder {}

    @NotBlank private final String houseCode;
}
