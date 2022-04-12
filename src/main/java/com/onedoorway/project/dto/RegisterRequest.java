package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = RegisterRequest.RegisterRequestBuilder.class)
public class RegisterRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class RegisterRequestBuilder {}

    @Email private final String email;

    @NotBlank
    @Size(min = 5)
    private final String password;

    @NotBlank private final String firstName;
    @NotBlank private final String lastName;
    private final String phone;
    private final String mobile;
}
