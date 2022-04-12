package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonDeserialize(builder = ListNoticeRequest.ListNoticeRequestBuilder.class)
public class ListNoticeRequest {

    @JsonPOJOBuilder(withPrefix = "")
    public static class ListNoticeRequestBuilder {}

    private final String houseCode;
    private final String status;
    private final Integer pageSize;
    private final Integer pageNumber;
}
