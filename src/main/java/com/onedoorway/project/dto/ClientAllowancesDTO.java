package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAllowancesDTO {
    private long id;
    private String cappedKMs;
    private String concessionCard;
    private String kms;
    private String grocerySpend;
    private String budgetlyCardNo;
    private Boolean deleted;
    private String lastUploadedBy;
}
