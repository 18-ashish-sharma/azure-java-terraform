package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ClientTransportRequest.ClientTransportRequestBuilder.class)
public class ClientTransportRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ClientTransportRequestBuilder {}

    private String odCar;
    @NotNull private long clientId;
    private String carRegistration;
    private LocalDate carRegExpiry;
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
