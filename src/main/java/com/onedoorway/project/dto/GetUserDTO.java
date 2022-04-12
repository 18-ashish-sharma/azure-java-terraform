package com.onedoorway.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String mobile;
    private Boolean deleted;
}
