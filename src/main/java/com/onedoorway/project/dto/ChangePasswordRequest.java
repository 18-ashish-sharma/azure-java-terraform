package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ChangePasswordRequest.ChangePasswordRequestBuilder.class)
public class ChangePasswordRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ChangePasswordRequestBuilder {}

    @NotBlank private final String oldPassword;
    @NotBlank private final String newPassword;
}
