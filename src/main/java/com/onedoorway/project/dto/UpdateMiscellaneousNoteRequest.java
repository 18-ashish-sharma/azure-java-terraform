package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(
        builder = UpdateMiscellaneousNoteRequest.UpdateMiscellaneousNoteRequestBuilder.class)
public class UpdateMiscellaneousNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateMiscellaneousNoteRequestBuilder {}

    private final String subject;
    private final String content;
    private final String house;
    private final String client;
    private final String user;
    private final String lastUploadedBy;
    private final Boolean deleted;
}
