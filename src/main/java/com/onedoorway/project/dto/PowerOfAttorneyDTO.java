package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerOfAttorneyDTO {
    private long id;
    private String clientName;
    private String name;
    private String type;
    private String phone;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postCode;
    private Boolean deleted;
    private String lastUpdatedBy;
}
