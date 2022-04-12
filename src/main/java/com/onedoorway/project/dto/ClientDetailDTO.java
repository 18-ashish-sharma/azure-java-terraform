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
public class ClientDetailDTO {

    private long id;
    private String email;
    private String name;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String phone;
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String state;
    private String postCode;
    private String house;
    private Boolean deleted;
    private long medicareNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    private boolean healthFund;
    private String centerLinkNo;
    private String medicareCardName;
    private String individualReferenceNumber;
    private String ndisNumber;
    private String photo;
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
