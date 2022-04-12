package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateClientRequest.UpdateClientRequestBuilder.class)
public class UpdateClientRequest {

    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateClientRequestBuilder {}

    private final long medicareNo;
    private final String centerLinkNo;
    private final Boolean healthFund;
    private final String expiryDate;
    private final String individualReferenceNumber;
    private final String medicareCardName;

    private String identity;
    private String culture;
    private String language;
    private String diagnosis;
    private String mobility;
    private String communication;
    private String medicationSupport;
    private String transportation;
    private String justiceOrders;
    private String supportRatio;
    private String shiftTimes;
    private String supportWorkerSpecs;
}
