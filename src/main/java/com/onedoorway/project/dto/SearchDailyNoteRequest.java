package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = SearchDailyNoteRequest.SearchDailyNoteRequestBuilder.class)
public class SearchDailyNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class SearchDailyNoteRequestBuilder {}

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant end;

    private final Integer pageSize;
    private final Integer pageNumber;
    private final String houseCode;
    private final Long clientId;
}
