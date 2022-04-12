package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ListHandoverSummaryRequest.ListHandoverSummaryRequestBuilder.class)
public class ListHandoverSummaryRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListHandoverSummaryRequestBuilder {}

    private final String start;
    private final String end;
    private final Integer pageSize;
    private final Integer pageNumber;
    private final String houseCode;
}
