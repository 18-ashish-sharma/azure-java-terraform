package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HouseDTO {
    private long id;
    private String houseCode;
    private String phone;
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String state;
    private Boolean deleted;
    private String postCode;
    private long totalClients;
    private long totalUsers;
}
