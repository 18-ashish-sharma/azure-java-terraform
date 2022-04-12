package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.ListMiscellaneousNoteRequest;
import com.onedoorway.project.dto.MiscellaneousNoteDTO;
import com.onedoorway.project.dto.MiscellaneousNoteRequest;
import com.onedoorway.project.dto.UpdateMiscellaneousNoteRequest;
import com.onedoorway.project.exception.MiscellaneousNoteServiceException;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.model.MiscellaneousNote;
import com.onedoorway.project.repository.LookupRepository;
import com.onedoorway.project.repository.MiscellaneousNoteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class MiscellaneousNoteService {
    private final MiscellaneousNoteRepository miscellaneousNoteRepository;
    private final LookupRepository lookupRepository;
    private final Context context;

    @Autowired
    public MiscellaneousNoteService(
            MiscellaneousNoteRepository miscellaneousNoteRepository,
            LookupRepository lookupRepository,
            Context context) {
        this.miscellaneousNoteRepository = miscellaneousNoteRepository;
        this.lookupRepository = lookupRepository;
        this.context = context;
    }

    public void createMiscellaneousNote(MiscellaneousNoteRequest request)
            throws MiscellaneousNoteServiceException {
        log.info("Creating miscellaneous note for category id {}", request.getCategoryId());
        Optional<Lookup> lookup = lookupRepository.findById(request.getCategoryId());
        if (lookup.isEmpty()) {
            log.error("Category not found for requested categoryId {}", request.getCategoryId());
            throw new MiscellaneousNoteServiceException("Category not found");
        }
        MiscellaneousNote miscellaneousNote =
                MiscellaneousNote.builder()
                        .category(lookup.get())
                        .noteDate(LocalDate.parse(request.getNoteDate()))
                        .content(request.getContent())
                        .subject(request.getSubject())
                        .lastUploadedBy(request.getLastUploadedBy())
                        .lastUpdatedAt(context.now())
                        .house(request.getHouse())
                        .client(request.getClient())
                        .user(request.getUser())
                        .deleted(false)
                        .build();
        miscellaneousNoteRepository.save(miscellaneousNote);
        log.info("Created miscellaneous note with id {} ", miscellaneousNote.getId());
    }

    public void updateMiscellaneousNote(Long id, UpdateMiscellaneousNoteRequest request)
            throws MiscellaneousNoteServiceException {
        log.info("Updating the miscellaneous note with id {}", id);
        Optional<MiscellaneousNote> miscellaneousNote = miscellaneousNoteRepository.findById(id);
        if (miscellaneousNote.isEmpty()) {
            String errorMessage =
                    String.format("Cannot get a miscellaneous note for the id %d", id);
            log.error(errorMessage);
            throw new MiscellaneousNoteServiceException(errorMessage);
        }
        MiscellaneousNote existingEntity = miscellaneousNote.get();

        existingEntity.setSubject(request.getSubject());
        existingEntity.setContent(request.getContent());
        existingEntity.setHouse(request.getHouse());
        existingEntity.setClient(request.getClient());
        existingEntity.setUser(request.getUser());
        existingEntity.setLastUploadedBy(request.getLastUploadedBy());
        existingEntity.setDeleted(request.getDeleted());
        existingEntity.setLastUpdatedAt(context.now());

        miscellaneousNoteRepository.save(existingEntity);
        log.info("Updated the miscellaneous note for the given id {}", id);
    }

    public List<MiscellaneousNoteDTO> listMiscellaneousNotes(ListMiscellaneousNoteRequest request) {
        List<MiscellaneousNote> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(),
                        request.getPageSize(),
                        Sort.by("noteDate").descending());
        ModelMapper modelMapper = new ModelMapper();
        log.info("Fetching miscellaneous notes based on request {}", request);
        res =
                miscellaneousNoteRepository.findAllByCategory_NameAndNoteDateBetween(
                        request.getCategory(),
                        LocalDate.parse(request.getStart()),
                        LocalDate.parse(request.getEnd()),
                        page);
        log.info("Fetched the miscellaneous notes based on the request");
        return res.stream()
                .map(item -> modelMapper.map(item, MiscellaneousNoteDTO.class))
                .collect(Collectors.toList());
    }
}
