package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateClientTransportRequest.UpdateClientTransportRequestBuilder.class)
public class UpdateClientTransportRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateClientTransportRequestBuilder {}

    private String odCar;
    private String carRegistration;
    private Boolean deleted;
    private String carRegExpiry;
    private String carModel;
    private String carMakeYear;
    private String isTravelProtocol;
    private String travelProtocol;
    private String comprehensiveInsurance;
    private String insurancePolicyNumber;
    private String authorisedPerson;
    private String authorisedPersonContactNumber;
    private String roadSideAssistanceCovered;
    private String insuranceAgency;
    private String insuranceContactNumber;
    private String lastUploadedBy;
    private String cappedKMs;
}
