package com.onedoorway.project.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortDTO {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roleNames;
}
