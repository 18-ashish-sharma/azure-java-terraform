package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = AddClientRequest.AddClientRequestBuilder.class)
public class AddClientRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class AddClientRequestBuilder {}

    @NotNull private final Long clientId;
    @NotBlank private final String houseCode;
}
