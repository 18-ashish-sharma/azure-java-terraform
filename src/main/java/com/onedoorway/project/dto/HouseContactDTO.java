package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseContactDTO {
    private long id;
    private List<HouseDTO> houses;
    private String caption;
    private String status;
    private String firstName;
    private String lastName;
    private String email;
    private String phone1;
    private String phone2;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String notes;
    private String lastUpdatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
