package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = RemoveRoleRequest.RemoveRoleRequestBuilder.class)
public class RemoveRoleRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class RemoveRoleRequestBuilder {}

    private final long userId;
    private final long roleId;
}
