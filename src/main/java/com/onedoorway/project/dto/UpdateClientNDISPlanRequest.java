package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateClientNDISPlanRequest.UpdateClientNDISPlanRequestBuilder.class)
public class UpdateClientNDISPlanRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateClientNDISPlanRequestBuilder {}

    private final String startDate;
    private final String endDate;
    private final String fundingType;
    private final String level;
    private final Boolean deleted;
    private final String lastUploadedBy;
}
