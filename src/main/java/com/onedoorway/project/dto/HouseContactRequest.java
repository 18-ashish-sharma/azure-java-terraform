package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = HouseContactRequest.HouseContactRequestBuilder.class)
public class HouseContactRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class HouseContactRequestBuilder {}

    @NotEmpty private final List<String> houseCode;
    private final String caption;
    @NotBlank private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone1;
    private final String phone2;
    private final String address1;
    private final String address2;
    private final String city;
    private final String state;
    private final String zip;
    private final String notes;
    private final String lastUpdatedBy;
}
