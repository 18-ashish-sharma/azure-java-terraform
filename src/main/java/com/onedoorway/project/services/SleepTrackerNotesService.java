package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.SleepTrackerNotesDTO;
import com.onedoorway.project.dto.SleepTrackerNotesRequest;
import com.onedoorway.project.dto.UpdateSleepTrackerRequest;
import com.onedoorway.project.exception.SleepTrackerNotesServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.SleepTrackerNotes;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.SleepTrackerNotesRepository;
import java.time.Instant;
import java.time.LocalDate;
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
public class SleepTrackerNotesService {
    private final SleepTrackerNotesRepository sleepTrackerNotesRepository;
    private final ClientRepository clientRepository;
    private final Context context;

    @Autowired
    public SleepTrackerNotesService(
            SleepTrackerNotesRepository sleepTrackerNotesRepository,
            ClientRepository clientRepository,
            Context context) {
        this.sleepTrackerNotesRepository = sleepTrackerNotesRepository;
        this.clientRepository = clientRepository;
        this.context = context;
    }

    public void createSleepTrackerNotes(SleepTrackerNotesRequest request)
            throws SleepTrackerNotesServiceException {
        log.info("Creating sleep tracker notes for client id {}", request.getClientId());

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new SleepTrackerNotesServiceException("Client not found");
        }
        if (sleepTrackerNotesRepository.findByClient_IdAndReportDate(
                        request.getClientId(), request.getReportDate())
                != null) {
            log.error(
                    "sleep tracker note already exists for client id {} for report date {}",
                    request.getClientId(),
                    request.getReportDate());
            throw new SleepTrackerNotesServiceException(
                    "sleep tracker already exists for client id for the given date");
        }

        SleepTrackerNotes sleepTrackerNotes =
                SleepTrackerNotes.builder()
                        .client(client.get())
                        .firstSlot(request.getFirstSlot())
                        .firstUpdatedBy(request.getFirstUpdatedBy())
                        .secondSlot(request.getSecondSlot())
                        .secondUpdatedBy(request.getSecondUpdatedBy())
                        .thirdSlot(request.getThirdSlot())
                        .thirdUpdatedBy(request.getThirdUpdatedBy())
                        .fourthSlot(request.getFourthSlot())
                        .fourthUpdatedBy(request.getFourthUpdatedBy())
                        .fifthSlot(request.getFifthSlot())
                        .fifthUpdatedBy(request.getFifthUpdatedBy())
                        .sixthSlot(request.getSixthSlot())
                        .sixthUpdatedBy(request.getSixthUpdatedBy())
                        .seventhSlot(request.getSeventhSlot())
                        .seventhUpdatedBy(request.getSeventhUpdatedBy())
                        .eighthSlot(request.getEighthSlot())
                        .eighthUpdatedBy(request.getEighthUpdatedBy())
                        .reportDate(request.getReportDate())
                        .lastUpdatedAt(context.now())
                        .build();
        sleepTrackerNotesRepository.save(sleepTrackerNotes);
        log.info("Created sleepTrackerNotes with id {} ", sleepTrackerNotes.getId());
    }

    public SleepTrackerNotesDTO getSleepTrackerNote(long clientId, String reportDate)
            throws SleepTrackerNotesServiceException {
        ModelMapper modelMapper = new ModelMapper();
        LocalDate date = LocalDate.parse(reportDate);
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new SleepTrackerNotesServiceException("Client not found " + clientId);
        }
        SleepTrackerNotes sleepTrackerNotes =
                sleepTrackerNotesRepository.findByClient_IdAndReportDate(clientId, date);
        if (sleepTrackerNotes == null) {
            String errorMessage =
                    String.format("Cannot get a sleepTrackerNote for the client %d", clientId);
            log.error(errorMessage);
            throw new SleepTrackerNotesServiceException(errorMessage);
        }
        return modelMapper.map(sleepTrackerNotes, SleepTrackerNotesDTO.class);
    }

    public void updateSleepTracker(long id, UpdateSleepTrackerRequest request)
            throws SleepTrackerNotesServiceException {
        log.info("Updating the sleep tracker note with id {}", id);
        Optional<SleepTrackerNotes> sleepTrackerNotes = sleepTrackerNotesRepository.findById(id);
        if (sleepTrackerNotes.isEmpty()) {
            String errorMessage =
                    String.format("Cannot get a sleep tracker for the id %d", request.getId());
            log.error(errorMessage);
            throw new SleepTrackerNotesServiceException(errorMessage);
        }
        Instant lastUpdatedAt =
                sleepTrackerNotes.get().getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS);
        if (!request.getCurrentLastUpdatedAt().equals(lastUpdatedAt)) {
            String errorMessage =
                    String.format(
                            "Cannot update sleep tracker note with lastUpdated %s",
                            request.getCurrentLastUpdatedAt());
            log.error(errorMessage);
            throw new SleepTrackerNotesServiceException(errorMessage);
        }

        SleepTrackerNotes existingEntity = sleepTrackerNotes.get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.addMappings(
                new PropertyMap<SleepTrackerNotesRequest, SleepTrackerNotes>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        mapper.map(request, existingEntity);
        existingEntity.setLastUpdatedAt(context.now());
        sleepTrackerNotesRepository.save(existingEntity);
        log.info("Updated the sleep tracker for the given id {}", request.getId());
    }
}
