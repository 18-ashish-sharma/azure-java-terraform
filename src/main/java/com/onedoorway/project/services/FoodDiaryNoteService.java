package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.FoodDiaryNoteDTO;
import com.onedoorway.project.dto.FoodDiaryNoteRequest;
import com.onedoorway.project.dto.ParticularFoodDiaryNoteRequest;
import com.onedoorway.project.exception.FoodDiaryNoteServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.FoodDiaryNote;
import com.onedoorway.project.model.MealType;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FoodDiaryNoteRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class FoodDiaryNoteService {
    private final FoodDiaryNoteRepository foodDiaryNoteRepository;
    private final ClientRepository clientRepository;
    private final Context context;

    @Autowired
    public FoodDiaryNoteService(
            FoodDiaryNoteRepository foodDiaryNoteRepository,
            ClientRepository clientRepository,
            Context context) {
        this.foodDiaryNoteRepository = foodDiaryNoteRepository;
        this.clientRepository = clientRepository;
        this.context = context;
    }

    public void createFoodReport(FoodDiaryNoteRequest request)
            throws FoodDiaryNoteServiceException {
        log.info("Creating food diary note for client id {}", request.getClientId());

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new FoodDiaryNoteServiceException("Client not found");
        }

        MealType mealType = MealType.valueOf(request.getMealType());
        log.info("meal type is {}", mealType);

        if (foodDiaryNoteRepository.findByClient_IdAndMealTypeAndReportDate(
                        request.getClientId(), mealType, request.getReportDate())
                != null) {
            log.error(
                    "food diary already exists for meal type {} and client id {} for report date {}",
                    mealType,
                    request.getClientId(),
                    request.getReportDate());
            throw new FoodDiaryNoteServiceException(
                    "food diary already exists for meal type and client id for the given date");
        }

        FoodDiaryNote foodDiaryNote =
                FoodDiaryNote.builder()
                        .client(client.get())
                        .mealTime(
                                LocalDateTime.parse(
                                        request.getMealTime(),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .mealFood(request.getMealFood())
                        .mealDrink(request.getMealDrink())
                        .mealComments(request.getMealComments())
                        .mealUpdatedBy(request.getMealUpdatedBy())
                        .mealType(mealType)
                        .reportDate(request.getReportDate())
                        .lastUpdatedAt(context.now())
                        .build();
        foodDiaryNoteRepository.save(foodDiaryNote);
        log.info("Created report with id {}", foodDiaryNote.getId());
    }

    public FoodDiaryNoteDTO getFoodDiaryNote(long clientId, String mealType)
            throws FoodDiaryNoteServiceException {
        ModelMapper modelMapper = new ModelMapper();
        LocalDate date = LocalDate.now();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new FoodDiaryNoteServiceException("Client not found " + clientId);
        }
        MealType type = MealType.valueOf(mealType);
        log.info("meal type is {}", mealType);
        FoodDiaryNote foodDiaryNote =
                foodDiaryNoteRepository.findByClient_IdAndMealTypeAndReportDate(
                        clientId, type, date);
        if (foodDiaryNote == null) {
            String errorMessage =
                    String.format("Cannot get an food diary for the client %d", clientId);
            log.error(errorMessage);
            throw new FoodDiaryNoteServiceException(errorMessage);
        }
        return modelMapper.map(foodDiaryNote, FoodDiaryNoteDTO.class);
    }

    public void updateFoodDiaryNote(Long id, FoodDiaryNoteRequest request)
            throws FoodDiaryNoteServiceException {
        log.info("Updating the food diary note with id {}", id);
        Optional<FoodDiaryNote> foodDiaryNote = foodDiaryNoteRepository.findById(id);
        if (foodDiaryNote.isEmpty()) {
            String errorMessage =
                    String.format("Cannot get a food diary for the id %d", request.getId());
            log.error(errorMessage);
            throw new FoodDiaryNoteServiceException(errorMessage);
        }
        Instant lastUpdatedAt =
                foodDiaryNote.get().getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS);
        if (!request.getCurrentLastUpdatedAt().equals(lastUpdatedAt)) {
            String errorMessage =
                    String.format(
                            "Cannot update food diary note with lastUpdated %s",
                            request.getCurrentLastUpdatedAt());
            log.error(errorMessage);
            throw new FoodDiaryNoteServiceException(errorMessage);
        }

        FoodDiaryNote existingEntity = foodDiaryNote.get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.addMappings(
                new PropertyMap<FoodDiaryNoteRequest, FoodDiaryNote>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        mapper.map(request, existingEntity);
        existingEntity.setMealTime(
                LocalDateTime.parse(
                        request.getMealTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        existingEntity.setLastUpdatedAt(context.now());
        foodDiaryNoteRepository.save(existingEntity);
        log.info("Updated the food diary for the given id {}", request.getId());
    }

    public FoodDiaryNoteDTO getParticularFoodDiaryNote(ParticularFoodDiaryNoteRequest request)
            throws FoodDiaryNoteServiceException {
        ModelMapper modelMapper = new ModelMapper();
        LocalDate date = LocalDate.parse(request.getReportDate());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.info("client not found {}", request.getClientId());
            throw new FoodDiaryNoteServiceException("Client not found " + request.getClientId());
        }
        MealType type = MealType.valueOf(request.getMealType());
        log.info("meal type is {}", request.getMealType());
        FoodDiaryNote foodDiaryNote =
                foodDiaryNoteRepository.findByClient_IdAndMealTypeAndReportDate(
                        request.getClientId(), type, date);
        if (foodDiaryNote == null) {
            String errorMessage =
                    String.format(
                            "Cannot get an food diary for the client %d", request.getClientId());
            log.error(errorMessage);
            throw new FoodDiaryNoteServiceException(errorMessage);
        }
        return modelMapper.map(foodDiaryNote, FoodDiaryNoteDTO.class);
    }
}
