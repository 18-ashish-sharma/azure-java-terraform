package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(
        builder = ParticularFoodDiaryNoteRequest.ParticularFoodDiaryNoteRequestBuilder.class)
public class ParticularFoodDiaryNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ParticularFoodDiaryNoteRequestBuilder {}

    @NotNull private long clientId;
    @NotBlank private final String reportDate;
    private String mealType;
}
