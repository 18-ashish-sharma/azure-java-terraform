package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ClientListDTO {
    private long id;
    private String email;
    private String name;
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String phone;
    private String addrLine1;
    private String addrLine2;
    private String city;
    private String state;
    private String postCode;
    private String house;
    private Boolean deleted;
}
