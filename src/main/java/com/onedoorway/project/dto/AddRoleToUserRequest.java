package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = AddRoleToUserRequest.AddRoleToUserRequestBuilder.class)
public class AddRoleToUserRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class AddRoleToUserRequestBuilder {}

    private final long userId;
    private final long roleId;
}
