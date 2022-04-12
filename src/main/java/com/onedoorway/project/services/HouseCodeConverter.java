package com.onedoorway.project.services;

import com.onedoorway.project.model.House;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;

public class HouseCodeConverter extends AbstractConverter<Set<House>, List<String>> {

    @Override
    protected List<String> convert(Set<House> houses) {
        return houses.stream().map(House::getHouseCode).collect(Collectors.toList());
    }
}
