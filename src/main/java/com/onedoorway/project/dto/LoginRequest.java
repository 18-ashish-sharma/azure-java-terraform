package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = LoginRequest.LoginRequestBuilder.class)
public class LoginRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class LoginRequestBuilder {}

    private final String email;
    private final String password;
}
