package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = FoodDiaryNoteRequest.FoodDiaryNoteRequestBuilder.class)
public class FoodDiaryNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class FoodDiaryNoteRequestBuilder {}

    private Long id;
    @NotNull private long clientId;
    private String mealType;
    private String mealTime;
    private String mealFood;
    private String mealDrink;
    private String mealComments;
    private String mealUpdatedBy;
    private LocalDate reportDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant currentLastUpdatedAt;
}
