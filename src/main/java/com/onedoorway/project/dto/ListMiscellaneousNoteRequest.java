package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ListMiscellaneousNoteRequest.ListMiscellaneousNoteRequestBuilder.class)
public class ListMiscellaneousNoteRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListMiscellaneousNoteRequestBuilder {}

    private final String start;
    private final String end;
    private final Integer pageSize;
    private final Integer pageNumber;
    private final String category;
}
