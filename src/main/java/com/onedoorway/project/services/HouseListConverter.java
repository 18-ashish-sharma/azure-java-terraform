package com.onedoorway.project.services;

import com.onedoorway.project.dto.HouseDTO;
import com.onedoorway.project.model.House;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

public class HouseListConverter extends AbstractConverter<Set<House>, List<HouseDTO>> {

    @Override
    protected List<HouseDTO> convert(Set<House> houses) {
        return houses.stream()
                .map(item -> new ModelMapper().map(item, HouseDTO.class))
                .collect(Collectors.toList());
    }
}
