package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ServiceProviderRequest.ServiceProviderRequestBuilder.class)
public class ServiceProviderRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ServiceProviderRequestBuilder {}

    @NotNull private long clientId;
    private final String name;
    private final String service;
    private String phone;
    private String email;
    private final String lastUpdatedBy;
}
