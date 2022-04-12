package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = GetHouseByCodeRequest.GetHouseByCodeRequestBuilder.class)
public class GetHouseByCodeRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class GetHouseByCodeRequestBuilder {}

    @NotBlank private final String houseCode;
}
