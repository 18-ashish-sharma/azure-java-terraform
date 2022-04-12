package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.CaseNoteDTO;
import com.onedoorway.project.dto.CaseNoteRequest;
import com.onedoorway.project.dto.ListCaseNoteRequest;
import com.onedoorway.project.dto.UpdateCaseNoteRequest;
import com.onedoorway.project.exception.CaseNoteServiceException;
import com.onedoorway.project.model.CaseNote;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.repository.CaseNoteRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.LookupRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CaseNoteService {
    private final CaseNoteRepository caseNoteRepository;
    private final ClientRepository clientRepository;
    private final LookupRepository lookupRepository;
    private final Context context;

    @Autowired
    public CaseNoteService(
            CaseNoteRepository caseNoteRepository,
            ClientRepository clientRepository,
            LookupRepository lookupRepository,
            Context context) {
        this.caseNoteRepository = caseNoteRepository;
        this.clientRepository = clientRepository;
        this.lookupRepository = lookupRepository;
        this.context = context;
    }

    public void createCaseNote(CaseNoteRequest request) throws CaseNoteServiceException {
        log.info("Creating case note for client id {}", request.getClientId());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new CaseNoteServiceException("Client not found");
        }
        log.info("Creating case note for category id {}", request.getCategoryId());
        Optional<Lookup> lookup = lookupRepository.findById(request.getCategoryId());
        if (lookup.isEmpty()) {
            log.error("Category not found for requested categoryId {}", request.getCategoryId());
            throw new CaseNoteServiceException("Category not found");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            CaseNote caseNote =
                    CaseNote.builder()
                            .client(client.get())
                            .noteDate(LocalDate.parse(request.getNoteDate()))
                            .category(lookup.get())
                            .content(request.getContent())
                            .subject(request.getSubject())
                            .lastUploadedBy(request.getLastUploadedBy())
                            .lastUpdatedAt(Instant.now())
                            .deleted(false)
                            .build();
            caseNoteRepository.save(caseNote);
            log.info("Created case note with id {}", caseNote.getId());
        } else {
            CaseNote caseNote =
                    CaseNote.builder()
                            .client(client.get())
                            .noteDate(LocalDate.parse(request.getNoteDate()))
                            .category(lookup.get())
                            .content(request.getContent())
                            .subject(request.getSubject())
                            .startTime(
                                    LocalDateTime.parse(
                                            request.getStartTime(),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .endTime(
                                    LocalDateTime.parse(
                                            request.getEndTime(),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .lastUploadedBy(request.getLastUploadedBy())
                            .lastUpdatedAt(Instant.now())
                            .deleted(false)
                            .build();
            caseNoteRepository.save(caseNote);
        }
    }

    public CaseNoteDTO getCaseNote(long id) throws CaseNoteServiceException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<CaseNote> caseNote = caseNoteRepository.findById(id);
        if (caseNote.isEmpty()) {
            throw new CaseNoteServiceException("no case not found with id " + id);
        }
        log.info("Fetched the incident with id {}", id);
        return modelMapper.map(caseNote.get(), CaseNoteDTO.class);
    }

    public List<CaseNoteDTO> listCaseNotes(ListCaseNoteRequest request) {
        List<CaseNote> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(),
                        request.getPageSize(),
                        Sort.by("noteDate").descending());
        ModelMapper modelMapper = new ModelMapper();
        log.info("Fetching case notes based on request {}", request);
        if (request.getStart() == null || request.getEnd() == null) {
            log.info("entering into loop null");
            res =
                    caseNoteRepository.findAllByCategory_NameAndClient_Id(
                            request.getCategory(), request.getClientId(), page);
        } else {
            log.info("entering into loop not null");
            res =
                    caseNoteRepository.findAllByCategory_NameAndClient_IdAndNoteDateBetween(
                            request.getCategory(),
                            request.getClientId(),
                            LocalDate.parse(request.getStart()),
                            LocalDate.parse(request.getEnd()),
                            page);
        }
        log.info("Fetched the case notes based on the request {}", res);
        return res.stream()
                .map(item -> modelMapper.map(item, CaseNoteDTO.class))
                .collect(Collectors.toList());
    }

    public void updateCaseNote(Long id, UpdateCaseNoteRequest request)
            throws CaseNoteServiceException {
        log.info("Updating the case note with id {}", id);
        Optional<CaseNote> caseNote = caseNoteRepository.findById(id);
        Optional<Lookup> lookup = lookupRepository.findById(request.getCategoryId());
        if (lookup.isEmpty()) {
            log.error("Category not found for requested categoryId {}", request.getCategoryId());
            throw new CaseNoteServiceException("Category not found");
        }
        if (caseNote.isPresent()) {
            if (request.getStartTime() == null || request.getEndTime() == null) {
                CaseNote existingEntity = caseNote.get();
                existingEntity.setDeleted(request.getDeleted());
                existingEntity.setContent(request.getContent());
                existingEntity.setSubject(request.getSubject());
                existingEntity.setLastUpdatedAt(context.now());
                existingEntity.setLastUploadedBy(request.getLastUploadedBy());
                existingEntity.setCategory(lookupRepository.getById(request.getCategoryId()));
                existingEntity.setNoteDate(LocalDate.parse(request.getNoteDate()));
                caseNoteRepository.save(existingEntity);
            } else {
                CaseNote existingEntity = caseNote.get();
                existingEntity.setDeleted(request.getDeleted());
                existingEntity.setContent(request.getContent());
                existingEntity.setCategory(lookupRepository.getById(request.getCategoryId()));
                existingEntity.setSubject(request.getSubject());
                existingEntity.setLastUpdatedAt(context.now());
                existingEntity.setLastUploadedBy(request.getLastUploadedBy());
                existingEntity.setStartTime(
                        LocalDateTime.parse(
                                request.getStartTime(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                existingEntity.setEndTime(
                        LocalDateTime.parse(
                                request.getEndTime(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                existingEntity.setNoteDate(LocalDate.parse(request.getNoteDate()));
                caseNoteRepository.save(existingEntity);
            }
            log.info("Updated the case note for the given id {}", id);
        } else {
            throw new CaseNoteServiceException("case note not found");
        }
    }
}
