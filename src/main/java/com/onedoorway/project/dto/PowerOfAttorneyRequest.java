package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = PowerOfAttorneyRequest.PowerOfAttorneyRequestBuilder.class)
public class PowerOfAttorneyRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class PowerOfAttorneyRequestBuilder {}

    @NotNull private final long id;
    private final long clientId;
    @NotBlank private final String type;
    @NotBlank private final String name;
    private final String phone;
    private final String email;
    private final String address1;
    private final String address2;
    private final String city;
    private final String state;
    private final String postCode;
    private final String lastUpdatedBy;
}
