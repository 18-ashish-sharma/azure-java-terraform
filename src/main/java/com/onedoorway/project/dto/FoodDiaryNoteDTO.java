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
public class FoodDiaryNoteDTO {
    private long diaryId;
    private String clientName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reportDate;

    private String mealType;
    private String mealFood;
    private String mealDrink;
    private String mealComments;
    private String mealUpdatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime mealTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
