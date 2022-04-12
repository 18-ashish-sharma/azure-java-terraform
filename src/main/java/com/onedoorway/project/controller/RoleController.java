package com.onedoorway.project.controller;

import com.onedoorway.project.dto.RoleDTO;
import com.onedoorway.project.services.RoleService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/roles", produces = "application/json")
public class RoleController {
    private final RoleService roleService;

    public RoleController(@Autowired RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<RoleDTO>> listRoles() {
        return new ResponseEntity<>(roleService.listAllUsers(), HttpStatus.OK);
    }
}
