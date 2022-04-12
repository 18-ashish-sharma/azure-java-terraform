package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ListUsersByPageRequest.ListUsersByPageRequestBuilder.class)
public class ListUsersByPageRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListUsersByPageRequestBuilder {}

    private final Integer pageSize;
    private final Integer pageNumber;
    private final String nameOrEmail;
    private final String houseCode;
}
