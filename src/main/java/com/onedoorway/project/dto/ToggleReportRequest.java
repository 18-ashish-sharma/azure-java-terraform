package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ToggleReportRequest.ToggleReportRequestBuilder.class)
public class ToggleReportRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ToggleReportRequestBuilder {}

    @NotNull private final Long clientId;
    @NotNull private final Long lookupId;
    private final Boolean toggle;
}
