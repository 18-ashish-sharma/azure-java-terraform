package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ListIncidentRequest.ListIncidentRequestBuilder.class)
public class ListIncidentRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListIncidentRequestBuilder {}

    private final Integer pageSize;
    private final Integer pageNumber;
    private final String houseCode;
    private final String clientName;
    private final Boolean reportedBy;
}
