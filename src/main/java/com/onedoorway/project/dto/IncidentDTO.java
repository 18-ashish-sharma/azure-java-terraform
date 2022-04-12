package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class IncidentDTO {
    private long id;
    private LookupDTO category;
    private LookupDTO type;
    private LookupDTO classification;
    private Boolean escalated;
    private UserDTO escalatedTo;
    private HouseDTO house;
    private ClientDTO client;
    private UserDTO reportedBy;
    private String status;
    private String raisedFor;
    private String description;
    private String witnessName;
    private String witnessDesignation;
    private String followUpResponsibility;
    private String policeReport;
    private String policeName;
    private String policeNumber;
    private String policeStation;
    private String beforeIncident;
    private String immediateAction;
    private String reportableToNDIS;
    private String reportableToWorksafe;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOccurred;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt;

    private String location;
    private String exactLocation;
    private String injuredGivenName;
    private String injuredFamilyName;
    private String closedBy;
    private String reviewedBy;
}
