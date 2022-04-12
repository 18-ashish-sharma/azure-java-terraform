package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = UpdateSleepTrackerRequest.UpdateSleepTrackerRequestBuilder.class)
public class UpdateSleepTrackerRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateSleepTrackerRequestBuilder {}

    private Long id;
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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant currentLastUpdatedAt;
}
