package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UserToHouseRequest.UserToHouseRequestBuilder.class)
public class UserToHouseRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UserToHouseRequestBuilder {}

    private final long userId;
    @NotBlank private final String houseCode;
}
