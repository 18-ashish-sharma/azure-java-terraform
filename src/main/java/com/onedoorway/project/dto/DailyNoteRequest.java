package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = DailyNoteRequest.DailyNoteRequestBuilder.class)
public class DailyNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class DailyNoteRequestBuilder {}

    @NotBlank private String note;
    @NotBlank private String houseCode;
    @NotNull private Long clientId;
    private String startTime;
    private String endTime;
}
