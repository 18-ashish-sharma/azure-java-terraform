package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateDailyNoteRequest.UpdateDailyNoteRequestBuilder.class)
public class UpdateDailyNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateDailyNoteRequestBuilder {}

    private final long id;
    @NotBlank private final String note;
    private final String startTime;
    private final String endTime;
}
