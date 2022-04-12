package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ListClientRequest.ListClientRequestBuilder.class)
public class ListClientRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListClientRequestBuilder {}

    private final Integer pageSize;
    private final Integer pageNumber;
    private final String nameOrHouse;
}
