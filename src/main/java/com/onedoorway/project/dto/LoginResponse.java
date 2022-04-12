package com.onedoorway.project.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private final String jwt;
    private final List<String> houseCode;
    private final List<String> roles;
    private final String firstName;
    private final String lastName;
}
