package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.NightReportDTO;
import com.onedoorway.project.dto.NightReportRequest;
import com.onedoorway.project.dto.ParticularNightReportRequest;
import com.onedoorway.project.exception.NightReportServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.NightReport;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.NightReportRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class NightReportService {
    private final NightReportRepository nightReportRepository;
    private final ClientRepository clientRepository;
    private final Context context;

    public NightReportService(
            NightReportRepository nightReportRepository,
            ClientRepository clientRepository,
            Context context) {
        this.nightReportRepository = nightReportRepository;
        this.clientRepository = clientRepository;
        this.context = context;
    }

    public void createNightReport(NightReportRequest request) throws NightReportServiceException {
        log.info("Creating night report for client id {}", request.getClientId());

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new NightReportServiceException("Client not found");
        }
        if (nightReportRepository.findByClient_IdAndReportDate(
                        request.getClientId(), request.getReportDate())
                != null) {
            log.error(
                    "Night Report already exists for client id {} for report date {}",
                    request.getClientId(),
                    request.getReportDate());
            throw new NightReportServiceException(
                    "Night Report already exists for client id for the given date");
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(
                new PropertyMap<NightReportRequest, NightReport>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        NightReport nightReport = modelMapper.map(request, NightReport.class);
        Client clientId = clientRepository.getById(request.getClientId());
        nightReport.setClient(clientId);
        nightReport.setReportDate(request.getReportDate());
        nightReport.setLastUpdatedAt(context.now());
        nightReportRepository.save(nightReport);
        log.info("Created nightReport with id {}", nightReport.getId());
    }

    public void updateReport(Long id, NightReportRequest request)
            throws NightReportServiceException {
        log.info("Updating the night report with id {}", id);
        Optional<NightReport> nightReport = nightReportRepository.findById(id);
        if (nightReport.isEmpty()) {
            String errorMessage =
                    String.format("Cannot get a night report for the id %d", request.getId());
            log.error(errorMessage);
            throw new NightReportServiceException(errorMessage);
        }
        Instant lastUpdatedAt = nightReport.get().getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS);
        if (!request.getCurrentLastUpdatedAt().equals(lastUpdatedAt)) {
            String errorMessage =
                    String.format(
                            "Cannot get a night report for the lastUpdated %s",
                            request.getCurrentLastUpdatedAt());
            log.error(errorMessage);
            throw new NightReportServiceException(errorMessage);
        }

        NightReport existingEntity = nightReport.get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.addMappings(
                new PropertyMap<NightReportRequest, NightReport>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        mapper.map(request, existingEntity);
        existingEntity.setLastUpdatedAt(context.now());
        nightReportRepository.save(existingEntity);
        log.info("Updated the night report for the given id {}", request.getId());
    }

    public NightReportDTO getNightReportById(long clientId) throws NightReportServiceException {
        ModelMapper modelMapper = new ModelMapper();
        LocalDate date = LocalDate.now();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new NightReportServiceException("Client not found " + clientId);
        }
        NightReport nightReport =
                nightReportRepository.findByClient_IdAndReportDate(clientId, date);
        if (nightReport == null) {
            String errorMessage =
                    String.format("Cannot get an nightReport for the client {}", clientId);
            log.error(errorMessage);
            throw new NightReportServiceException(errorMessage);
        }
        return modelMapper.map(nightReport, NightReportDTO.class);
    }

    public NightReportDTO getParticularNightReport(ParticularNightReportRequest request)
            throws NightReportServiceException {
        ModelMapper modelMapper = new ModelMapper();
        LocalDate date = LocalDate.parse(request.getReportDate());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.info("client not found {}", request.getClientId());
            throw new NightReportServiceException("Client not found " + request.getClientId());
        }
        NightReport nightReport =
                nightReportRepository.findByClient_IdAndReportDate(request.getClientId(), date);
        if (nightReport == null) {
            String errorMessage =
                    String.format(
                            "Cannot get an nightReport for the client {}", request.getClientId());
            log.error(errorMessage);
            throw new NightReportServiceException(errorMessage);
        }
        return modelMapper.map(nightReport, NightReportDTO.class);
    }
}
