package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = IncidentRequest.IncidentRequestBuilder.class)
public class IncidentRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class IncidentRequestBuilder {}

    private final Long id;
    private final Long categoryId;
    private final Long typeId;
    private final Long classificationId;
    private final Long houseId;
    private final Long clientId;
    @NotBlank private final String status;
    @NotBlank private final String raisedFor;
    @NotBlank private final String description;
    @NotBlank private final String dateOccurred;
    @NotBlank private final String location;
    @NotBlank private final String exactLocation;
    private final String injuredGivenName;
    private final String injuredFamilyName;
    private final String witnessName;
    private final String witnessDesignation;
    private final String followUpResponsibility;
    private final String policeReport;
    private final String policeName;
    private final String policeNumber;
    private final String policeStation;
    private final String beforeIncident;
    private final String immediateAction;
    private final String reportableToNDIS;
    private final String reportableToWorksafe;
}
