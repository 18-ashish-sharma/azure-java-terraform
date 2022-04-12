package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientContactDTO {
    private long id;
    private String clientName;
    private String relation;
    private String designation;
    private String firstName;
    private String lastName;
    private String email;
    private String phone1;
    private String phone2;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postCode;
    private String notes;
    private String status;
    private String lastUpdatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
