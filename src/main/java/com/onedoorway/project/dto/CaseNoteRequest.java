package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = CaseNoteRequest.CaseNoteRequestBuilder.class)
public class CaseNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class CaseNoteRequestBuilder {}

    @NotNull private final long clientId;
    @NotNull private final long categoryId;
    @NotBlank private final String noteDate;
    private final String startTime;
    private final String endTime;
    private final String content;
    private final String subject;
    private final String lastUploadedBy;
}
