package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = UpdateBowelNoteRequest.UpdateBowelNoteRequestBuilder.class)
public class UpdateBowelNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateBowelNoteRequestBuilder {}

    private final String recordTime;
    private final String size;
    private final Boolean type1;
    private final Boolean type2;
    private final Boolean type3;
    private final Boolean type4;
    private final Boolean type5;
    private final Boolean type6;
    private final Boolean type7;
    private final Boolean deleted;
    private final String lastUploadedBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant currentLastUpdatedAt;
}
