package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ClientNDISPlanRequest.ClientNDISPlanRequestBuilder.class)
public class ClientNDISPlanRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ClientNDISPlanRequestBuilder {}

    @NotNull private final long clientId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String fundingType;
    private final String level;
    private final Boolean deleted;
    private final String supportDocument;
    private final String otherDocument;
    private final String lastUploadedBy;
}
