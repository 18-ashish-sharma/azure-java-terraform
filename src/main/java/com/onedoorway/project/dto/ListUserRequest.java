package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ListUserRequest.ListUserRequestBuilder.class)
public class ListUserRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListUserRequestBuilder {}

    @NotBlank private final String houseCode;
}
