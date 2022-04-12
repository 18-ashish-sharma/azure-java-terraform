package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ClientAllowancesRequest.ClientAllowancesRequestBuilder.class)
public class ClientAllowancesRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ClientAllowancesRequestBuilder {}

    @NotNull private final long clientId;
    private String cappedKMs;
    private String concessionCard;
    private String kms;
    private String grocerySpend;
    private String budgetlyCardNo;
    private final String lastUploadedBy;
}
