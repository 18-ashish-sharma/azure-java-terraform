package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = MiscellaneousNoteRequest.MiscellaneousNoteRequestBuilder.class)
public class MiscellaneousNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class MiscellaneousNoteRequestBuilder {}

    @NotNull private final long categoryId;
    @NotBlank private final String noteDate;
    @NotBlank private final String subject;
    @NotBlank private final String content;
    private final String house;
    private final String client;
    private final String user;
    @NotBlank private final String lastUploadedBy;
}
