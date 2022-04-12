package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateUserRequest.UpdateUserRequestBuilder.class)
public class UpdateUserRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateUserRequestBuilder {}

    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String mobile;
    private final Boolean deleted;
}
