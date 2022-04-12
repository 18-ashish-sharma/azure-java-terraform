package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateFolderRequest.UpdateFolderRequestBuilder.class)
public class UpdateFolderRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateFolderRequestBuilder {}

    private final long id;
    private final String folderName;
    private final String status;
    private final String lastUpdatedBy;
}
