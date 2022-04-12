package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = BowelNoteRequest.BowelNoteRequestBuilder.class)
public class BowelNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class BowelNoteRequestBuilder {}

    private final long clientId;
    private final String startDate;
    private final String recordTime;

    private final String size;
    private final Boolean type1;
    private final Boolean type2;
    private final Boolean type3;
    private final Boolean type4;
    private final Boolean type5;
    private final Boolean type6;
    private final Boolean type7;
    private final String lastUploadedBy;
}
