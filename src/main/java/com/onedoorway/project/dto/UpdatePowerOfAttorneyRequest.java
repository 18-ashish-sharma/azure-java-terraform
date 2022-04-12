package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdatePowerOfAttorneyRequest.UpdatePowerOfAttorneyRequestBuilder.class)
public class UpdatePowerOfAttorneyRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdatePowerOfAttorneyRequestBuilder {}

    private final String name;
    private final String type;
    private final String phone;
    private final String email;
    private final String address1;
    private final String address2;
    private final String city;
    private final String state;
    private final String postCode;
    private Boolean deleted;
    private final String lastUpdatedBy;
}
