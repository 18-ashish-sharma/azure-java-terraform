package com.onedoorway.project.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListUsersByPageResponse {
    private List<UserDTO> users;
    private Long totalUsers;
}
