package com.onedoorway.project.services;

import com.onedoorway.project.dto.RoleDTO;
import com.onedoorway.project.repository.RoleRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(@Autowired RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDTO> listAllUsers() {
        List<RoleDTO> roles =
                roleRepository.findAll().stream()
                        .map(item -> new ModelMapper().map(item, RoleDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched the roles {}", roles.size());
        return roles;
    }
}
