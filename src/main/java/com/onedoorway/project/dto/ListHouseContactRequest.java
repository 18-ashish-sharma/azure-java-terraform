package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonDeserialize(builder = ListHouseContactRequest.ListHouseContactRequestBuilder.class)
public class ListHouseContactRequest {

    @JsonPOJOBuilder(withPrefix = "")
    public static class ListHouseContactRequestBuilder {}

    private final String houseCode;
    private final Integer pageSize;
    private final Integer pageNumber;
}
