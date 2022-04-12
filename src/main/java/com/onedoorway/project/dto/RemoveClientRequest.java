package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = RemoveClientRequest.RemoveClientRequestBuilder.class)
public class RemoveClientRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class RemoveClientRequestBuilder {}

    @NotBlank private final String houseCode;
    private final long clientId;
}
