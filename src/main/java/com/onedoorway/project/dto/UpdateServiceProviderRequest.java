package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateServiceProviderRequest.UpdateServiceProviderRequestBuilder.class)
public class UpdateServiceProviderRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateServiceProviderRequestBuilder {}

    private final String name;
    private final String service;
    private final Boolean deleted;
    private final String lastUpdatedBy;
    private final String email;
    private final String phone;
}
