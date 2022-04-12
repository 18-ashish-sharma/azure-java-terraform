package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ForgotPasswordRequest.ForgotPasswordRequestBuilder.class)
public class ForgotPasswordRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ForgotPasswordRequestBuilder {}

    @NotBlank private final String email;
}
