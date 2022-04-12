package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = SleepTrackerNotesRequest.SleepTrackerNotesRequestBuilder.class)
public class SleepTrackerNotesRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class SleepTrackerNotesRequestBuilder {}

    @NotNull private long clientId;
    private final String firstSlot;
    private final String firstUpdatedBy;
    private final String secondSlot;
    private final String secondUpdatedBy;
    private final String thirdSlot;
    private final String thirdUpdatedBy;
    private final String fourthSlot;
    private final String fourthUpdatedBy;
    private final String fifthSlot;
    private final String fifthUpdatedBy;
    private final String sixthSlot;
    private final String sixthUpdatedBy;
    private final String seventhSlot;
    private final String seventhUpdatedBy;
    private final String eighthSlot;
    private final String eighthUpdatedBy;
    @NotNull private final LocalDate reportDate;
}
