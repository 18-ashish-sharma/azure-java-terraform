package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ClientRequest.ClientRequestBuilder.class)
public class ClientRequest {

    @JsonPOJOBuilder(withPrefix = "")
    public static class ClientRequestBuilder {}

    @Email @NotBlank private final String email;
    @NotBlank private final String name;
    private final String gender;
    @NotBlank private final String dob;
    private final String phone;
    private final String addrLine1;
    private final String addrLine2;
    private final String city;
    private final String state;
    private final String postCode;
    private final Boolean deleted;
    private final String ndisNumber;
}
