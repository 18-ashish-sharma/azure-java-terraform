package com.onedoorway.project.services;

import com.onedoorway.project.dto.LookupDTO;
import com.onedoorway.project.model.LookupType;
import com.onedoorway.project.repository.LookupRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class LookupService {
    private final LookupRepository lookupRepository;

    public LookupService(@Autowired LookupRepository lookupRepository) {
        this.lookupRepository = lookupRepository;
    }

    public List<LookupDTO> listLookups(LookupType lookupType) {
        List<LookupDTO> lookupDTOS =
                lookupRepository.findAllByLookupType(lookupType).stream()
                        .map(item -> new ModelMapper().map(item, LookupDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched all lookUps - count {}", lookupDTOS.size());
        return lookupDTOS;
    }
}
