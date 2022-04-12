package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderDTO {
    private long id;
    private String clientName;
    private String name;
    private String service;
    private Boolean deleted;
    private String phone;
    private String email;
    private String lastUpdatedBy;
}
