package com.onedoorway.project.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.RoleDTO;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.repository.RoleRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock RoleRepository mockRoleRepository;

    private RoleService roleService;

    @BeforeEach
    void init() {
        roleService = new RoleService(mockRoleRepository);
    }

    @Test
    void testListAllUsers() {
        // Given
        when(mockRoleRepository.findAll())
                .thenReturn(
                        List.of(
                                Role.builder().name("ROLE 1").id(1).build(),
                                Role.builder().name("ROLE 2").id(2).build()));
        // When
        List<RoleDTO> roles = roleService.listAllUsers();
        // Then
        assertEquals(roles.size(), 2);
    }

    @Test
    void testListAllUsers_Empty() {
        // When
        List<RoleDTO> roles = roleService.listAllUsers();
        // Then
        assertEquals(roles.size(), 0);
    }
}
