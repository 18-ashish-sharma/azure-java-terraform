package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = GetHouseImageRequest.GetHouseImageRequestBuilder.class)
public class GetHouseImageRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class GetHouseImageRequestBuilder {}

    @NotBlank private final String houseCode;
}
