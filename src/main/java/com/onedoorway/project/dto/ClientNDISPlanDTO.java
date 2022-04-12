package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientNDISPlanDTO {
    private long id;
    private long clientId;
    private String startDate;
    private String endDate;
    private String fundingType;
    private String level;
    private Boolean deleted;
    private String supportDocument;
    private String otherDocument;
    private String lastUploadedBy;
}
