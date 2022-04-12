package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.HandoverSummaryDTO;
import com.onedoorway.project.dto.HandoverSummaryRequest;
import com.onedoorway.project.dto.ListHandoverSummaryRequest;
import com.onedoorway.project.dto.UpdateHandoverSummaryRequest;
import com.onedoorway.project.exception.HandoverSummaryServiceException;
import com.onedoorway.project.model.HandoverShift;
import com.onedoorway.project.model.HandoverSummary;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HandoverSummaryRepository;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class HandoverSummaryService {
    private final HandoverSummaryRepository handoverSummaryRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;
    private final Context context;

    @Autowired
    public HandoverSummaryService(
            HandoverSummaryRepository handoverSummaryRepository,
            HouseRepository houseRepository,
            UserRepository userRepository,
            Context context) {
        this.handoverSummaryRepository = handoverSummaryRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.context = context;
    }

    public void createHandoverSummary(HandoverSummaryRequest request)
            throws HandoverSummaryServiceException {
        log.info("Creating handOver summary for house code {}", request.getHouseCode());

        String email = context.currentUser();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }

        Optional<User> user1 = userRepository.findById(request.getHandoverToId());
        if (user1.isEmpty()) {
            log.error("User not found for requested houseId {}", request.getHandoverToId());
            throw new HandoverSummaryServiceException("no user found " + request.getHandoverToId());
        }

        House house = houseRepository.getByHouseCode(request.getHouseCode());
        if (house == null) {
            handleError("Cannot get a house for the house code %s", request.getHouseCode());
            throw new HandoverSummaryServiceException("House not found");
        }

        HandoverSummary handoverSummary =
                HandoverSummary.builder()
                        .house(house)
                        .handoverDate(LocalDate.parse(request.getHandoverDate()))
                        .handoverTime(
                                LocalDateTime.parse(
                                        request.getHandoverTime(),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .handoverById(user)
                        .deleted(false)
                        .handoverToId(user1.get())
                        .handoverShift(HandoverShift.valueOf(request.getHandoverShift()))
                        .behaviourSummary(request.getBehaviourSummary())
                        .sleepSummary(request.getSleepSummary())
                        .foodSummary(request.getFoodSummary())
                        .comments(request.getComments())
                        .communications(request.getCommunications())
                        .toiletingSummary(request.getToiletingSummary())
                        .activitiesSummary(request.getActivitiesSummary())
                        .peopleAttended(request.getPeopleAttended())
                        .placesVisited(request.getPlacesVisited())
                        .topPriorities(request.getTopPriorities())
                        .thingsLater(request.getThingsLater())
                        .lastUpdatedAt(context.now())
                        .build();
        handoverSummaryRepository.save(handoverSummary);

        log.info("Created handOver with id {}", handoverSummary.getId());
    }

    public List<HandoverSummaryDTO> listHandoverSummary(ListHandoverSummaryRequest request) {
        List<HandoverSummary> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(),
                        request.getPageSize(),
                        Sort.by("handoverDate").descending());
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        log.info("Fetching hand over summary based on request {}", request);
        res =
                handoverSummaryRepository.findAllByHouse_HouseCodeAndHandoverDateBetween(
                        request.getHouseCode(),
                        LocalDate.parse(request.getStart()),
                        LocalDate.parse(request.getEnd()),
                        page);
        log.info("Fetched the hand over summary based on the request");
        modelMapper
                .typeMap(HandoverSummary.class, HandoverSummaryDTO.class)
                .addMappings(
                        mapper -> {
                            mapper.map(
                                    src -> src.getHouse().getHouseCode(),
                                    HandoverSummaryDTO::setHouseCode);
                            mapper.map(
                                    src -> src.getHandoverById(),
                                    HandoverSummaryDTO::setHandoverBy);
                            mapper.map(
                                    src -> src.getHandoverToId(),
                                    HandoverSummaryDTO::setHandoverTo);
                        });
        return res.stream()
                .map(item -> modelMapper.map(item, HandoverSummaryDTO.class))
                .collect(Collectors.toList());
    }

    private void handleError(String message, String... params)
            throws HandoverSummaryServiceException {
        String errorMessage = String.format(message, params);
        log.error(errorMessage);
        throw new HandoverSummaryServiceException(errorMessage);
    }

    public void updateHandoverSummary(Long id, UpdateHandoverSummaryRequest request)
            throws HandoverSummaryServiceException {
        log.info("Updating the handover summary with id {}", id);

        Optional<HandoverSummary> handoverSummary = handoverSummaryRepository.findById(id);
        if (handoverSummary.isEmpty()) {
            String errorMessage = String.format("Cannot get a handover summary for the id %d", id);
            log.error(errorMessage);
            throw new HandoverSummaryServiceException(errorMessage);
        }

        String email = context.currentUser();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }

        Optional<User> user1 = userRepository.findById(request.getHandoverToId());
        if (user1.isEmpty()) {
            throw new HandoverSummaryServiceException("user not found");
        }

        HandoverSummary existingEntity = handoverSummary.get();
        existingEntity.setDeleted(request.getDeleted());
        existingEntity.setHandoverShift(HandoverShift.valueOf(request.getHandoverShift()));
        existingEntity.setHandoverToId(user1.get());
        existingEntity.setHandoverTime(
                LocalDateTime.parse(
                        request.getHandoverTime(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        existingEntity.setHandoverDate(LocalDate.parse(request.getHandoverDate()));
        existingEntity.setBehaviourSummary(request.getBehaviourSummary());
        existingEntity.setSleepSummary(request.getSleepSummary());
        existingEntity.setFoodSummary(request.getFoodSummary());
        existingEntity.setToiletingSummary(request.getToiletingSummary());
        existingEntity.setActivitiesSummary(request.getActivitiesSummary());
        existingEntity.setCommunications(request.getCommunications());
        existingEntity.setTopPriorities(request.getTopPriorities());
        existingEntity.setComments(request.getComments());
        existingEntity.setPeopleAttended(request.getPeopleAttended());
        existingEntity.setPlacesVisited(request.getPlacesVisited());
        existingEntity.setThingsLater(request.getThingsLater());
        existingEntity.setLastUpdatedAt(context.now());

        handoverSummaryRepository.save(existingEntity);
        log.info("Updated the handover summary for the given id {}", id);
    }
}
