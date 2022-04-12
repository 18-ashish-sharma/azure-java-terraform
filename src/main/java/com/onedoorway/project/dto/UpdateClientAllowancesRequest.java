package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(
        builder =
                com.onedoorway.project.dto.UpdateClientAllowancesRequest
                        .UpdateClientAllowancesRequestBuilder.class)
public class UpdateClientAllowancesRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateClientAllowancesRequestBuilder {}

    private String cappedKMs;
    private String concessionCard;
    private String kms;
    private String grocerySpend;
    private String budgetlyCardNo;
    private final Boolean deleted;
    private final String lastUploadedBy;
}
