package com.onedoorway.project.services;

import com.onedoorway.project.dto.DailyNoteDTO;
import com.onedoorway.project.dto.DailyNoteRequest;
import com.onedoorway.project.dto.SearchDailyNoteRequest;
import com.onedoorway.project.dto.UpdateDailyNoteRequest;
import com.onedoorway.project.exception.DailyNoteServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.DailyNote;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.DailyNoteRepository;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DailyNoteService {
    private final DailyNoteRepository dailyNoteRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public DailyNoteService(
            @Autowired DailyNoteRepository dailyNoteRepository,
            @Autowired HouseRepository houseRepository,
            @Autowired UserRepository userRepository,
            @Autowired ClientRepository clientRepository) {
        this.dailyNoteRepository = dailyNoteRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public void createDailyNote(DailyNoteRequest request) throws DailyNoteServiceException {
        DailyNote dailyNote;
        // 1. Get the user's email from token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            handleError("Unauthenticated session or no user found");
        }
        String email = authentication.getName();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }

        // 2. Get the house from the repo
        House house = houseRepository.getByHouseCode(request.getHouseCode());
        if (house == null) {
            handleError("Cannot get a house for the house code %s", request.getHouseCode());
        }
        // 3. Get the client from the repo
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            handleError(
                    "Cannot get a client for the clientId %s", request.getClientId().toString());
        }
        // 4. Create the entity to persist
        if (request.getStartTime() != null && request.getEndTime() != null) {
            dailyNote =
                    DailyNote.builder()
                            .note(request.getNote())
                            .house(house)
                            .client(client.get())
                            .createBy(user)
                            .startTime(
                                    LocalDateTime.parse(
                                            request.getStartTime(),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .endTime(
                                    LocalDateTime.parse(
                                            request.getEndTime(),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .createdAt(Instant.now())
                            .build();
        } else {
            dailyNote =
                    DailyNote.builder()
                            .note(request.getNote())
                            .house(house)
                            .client(client.get())
                            .createBy(user)
                            .createdAt(Instant.now())
                            .build();
        }
        // 5. Save the entity
        dailyNoteRepository.save(dailyNote);

        log.info("Created the daily note");
    }

    private void handleError(String message, String... params) throws DailyNoteServiceException {
        String errorMessage = String.format(message, params);
        log.error(errorMessage);
        throw new DailyNoteServiceException(errorMessage);
    }

    public List<DailyNoteDTO> getNotesByCode(String houseCode) {
        List<DailyNoteDTO> res =
                dailyNoteRepository
                        .findByHouse_HouseCode(houseCode, Sort.by("createdAt").descending())
                        .stream()
                        .map(item -> new ModelMapper().map(item, DailyNoteDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched the daily notes based on houseCode {}", houseCode);
        return res;
    }

    public List<DailyNoteDTO> getDailyNotes(String houseCode, long userId) {
        List<DailyNoteDTO> res =
                dailyNoteRepository.findByHouse_HouseCodeAndCreateBy_Id(houseCode, userId).stream()
                        .map(item -> new ModelMapper().map(item, DailyNoteDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched the daily notes based on houseCode {} and userId {}", houseCode, userId);
        return res;
    }

    public void deleteDailyNote(long id) {
        Optional<DailyNote> dailyNote = dailyNoteRepository.findById(id);
        if (dailyNote.isPresent()) {
            log.info("Deleted daily note with id {}", id);
            dailyNoteRepository.delete(dailyNote.get());
        }
    }

    public List<DailyNoteDTO> search(SearchDailyNoteRequest request) {
        log.info("Fetching the daily notes based on the request {}", request);
        List<DailyNoteDTO> res =
                dailyNoteRepository
                        .findAllByHouse_HouseCodeAndClient_IdAndCreatedAtBetween(
                                request.getHouseCode(),
                                request.getClientId(),
                                request.getStart(),
                                request.getEnd(),
                                PageRequest.of(
                                        request.getPageNumber(),
                                        request.getPageSize(),
                                        Sort.by("createdAt").descending()))
                        .stream()
                        .map(item -> new ModelMapper().map(item, DailyNoteDTO.class))
                        .collect(Collectors.toList());

        log.info("Fetched the daily notes based on the request");
        return res;
    }

    public void updateDailyNote(UpdateDailyNoteRequest request) throws DailyNoteServiceException {
        Optional<DailyNote> dailyNote = dailyNoteRepository.findById(request.getId());
        DailyNote note;
        // Checking whether Daily Note is Empty
        if (dailyNote.isEmpty()) {
            log.info("Daily note with id{} not found", request.getId());
            throw new DailyNoteServiceException("Daily note  not found");
        }
        if (request.getStartTime() != null && request.getEndTime() != null) {
            note = dailyNote.get();
            note.setNote(request.getNote());
            note.setStartTime(
                    LocalDateTime.parse(
                            request.getStartTime(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            note.setEndTime(
                    LocalDateTime.parse(
                            request.getEndTime(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            note = dailyNote.get();
            note.setNote(request.getNote());
        }
        dailyNoteRepository.save(note);
        log.info(
                "Updated the daily notes {} with  new note {} and given startTime{} and endTIme{}",
                request.getId(),
                request.getNote(),
                request.getStartTime(),
                request.getEndTime());
    }
}
