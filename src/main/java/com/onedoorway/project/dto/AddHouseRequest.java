package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = AddHouseRequest.AddHouseRequestBuilder.class)
public class AddHouseRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class AddHouseRequestBuilder {}

    @NotBlank private final String houseCode;
    private final String phone;
    @NotBlank private final String addrLine1;
    private final String addrLine2;
    @NotBlank private final String city;
    @NotBlank private final String state;
    private final Boolean deleted;

    @NotBlank
    @Pattern(regexp = "\\d{4}")
    private final String postCode;
}
