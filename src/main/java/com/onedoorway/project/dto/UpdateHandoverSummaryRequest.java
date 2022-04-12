package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sun.istack.NotNull;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateHandoverSummaryRequest.UpdateHandoverSummaryRequestBuilder.class)
public class UpdateHandoverSummaryRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateHandoverSummaryRequestBuilder {}

    @NotNull private long handoverToId;
    @NotNull private final String handoverDate;
    @NotBlank private final String handoverTime;
    @NotBlank private final String handoverShift;
    private final Boolean deleted;
    private final String behaviourSummary;
    private final String sleepSummary;
    private final String foodSummary;
    private final String toiletingSummary;
    private final String activitiesSummary;
    private final String communications;
    private final String topPriorities;
    private final String comments;
    private final String peopleAttended;
    private final String placesVisited;
    private final String thingsLater;
}
