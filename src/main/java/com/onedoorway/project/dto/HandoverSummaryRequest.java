package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = HandoverSummaryRequest.HandoverSummaryRequestBuilder.class)
public class HandoverSummaryRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class HandoverSummaryRequestBuilder {}

    @NotBlank private String houseCode;
    @NotNull private final String handoverDate;
    private final String handoverTime;
    @NotBlank private final String handoverShift;
    @NotNull private final long handoverToId;
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
    private final Boolean deleted;
    private final String thingsLater;
}
