package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ResetPasswordRequest.ResetPasswordRequestBuilder.class)
public class ResetPasswordRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ResetPasswordRequestBuilder {}

    @NotBlank private final String confirmPassword;
    @NotBlank private final String newPassword;
    @NotBlank private final String token;
}
