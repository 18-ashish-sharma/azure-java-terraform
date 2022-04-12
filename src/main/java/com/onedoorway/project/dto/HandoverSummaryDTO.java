package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandoverSummaryDTO {
    private long id;
    private Boolean Deleted;

    private String houseCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate handoverDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime handoverTime;

    private String handoverShift;
    private UserDTO handoverBy;

    private UserDTO handoverTo;
    private String behaviourSummary;
    private String sleepSummary;
    private String foodSummary;
    private Boolean deleted;
    private String toiletingSummary;
    private String activitiesSummary;
    private String communications;
    private String topPriorities;
    private String comments;
    private String peopleAttended;
    private String placesVisited;
    private String thingsLater;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
