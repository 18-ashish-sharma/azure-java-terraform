package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = FolderRequest.FolderRequestBuilder.class)
public class FolderRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class FolderRequestBuilder {}

    @NotNull private long clientId;
    private final String folderName;
    private final String lastUpdatedBy;
}
