package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientTransportDTO {
    private long id;
    private String odCar;
    private String carRegistration;
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
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
