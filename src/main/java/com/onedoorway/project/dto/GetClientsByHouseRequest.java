package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = GetClientsByHouseRequest.GetClientsByHouseRequestBuilder.class)
public class GetClientsByHouseRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class GetClientsByHouseRequestBuilder {}

    @NotBlank private final String houseCode;
}
