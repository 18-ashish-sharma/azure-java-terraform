package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = UpdateClientContactRequest.UpdateClientContactRequestBuilder.class)
public class UpdateClientContactRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class UpdateClientContactRequestBuilder {}

    @NotBlank private final String relation;
    private final String designation;
    @NotBlank private final String firstName;
    private final String lastName;
    @Email @NotBlank private final String email;
    private final String phone1;
    private final String phone2;
    private final String address1;
    private final String address2;
    private final String city;
    private final String state;
    private final String postCode;
    private final String notes;
    private final String status;
    private final String lastUpdatedBy;
}
