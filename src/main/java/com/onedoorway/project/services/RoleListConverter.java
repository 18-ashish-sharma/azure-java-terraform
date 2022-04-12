package com.onedoorway.project.services;

import com.onedoorway.project.model.Role;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;

public class RoleListConverter extends AbstractConverter<Set<Role>, List<String>> {

    @Override
    protected List<String> convert(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }
}
