package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateCaseNoteRequest.UpdateCaseNoteRequestBuilder.class)
public class UpdateCaseNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateCaseNoteRequestBuilder {}

    private final String startTime;
    private final String endTime;
    private final String noteDate;
    private final String content;
    private final String subject;
    private final Boolean deleted;
    private final String lastUploadedBy;
    private final Long categoryId;
}
