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
public class SleepTrackerNotesDTO {
    private long id;
    private String clientName;
    private long clientId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    private String firstSlot;
    private String firstUpdatedBy;
    private String secondSlot;
    private String secondUpdatedBy;
    private String thirdSlot;
    private String thirdUpdatedBy;
    private String fourthSlot;
    private String fourthUpdatedBy;
    private String fifthSlot;
    private String fifthUpdatedBy;
    private String sixthSlot;
    private String sixthUpdatedBy;
    private String seventhSlot;
    private String seventhUpdatedBy;
    private String eighthSlot;
    private String eighthUpdatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
