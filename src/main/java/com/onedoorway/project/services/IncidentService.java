package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.IncidentServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
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
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final ClientRepository clientRepository;
    private final HouseRepository houseRepository;
    private final LookupRepository lookupRepository;
    private final UserRepository userRepository;
    private final Context context;

    @Autowired
    public IncidentService(
            IncidentRepository incidentRepository,
            ClientRepository clientRepository,
            HouseRepository houseRepository,
            LookupRepository lookupRepository,
            UserRepository userRepository,
            Context context) {
        this.incidentRepository = incidentRepository;
        this.clientRepository = clientRepository;
        this.houseRepository = houseRepository;
        this.lookupRepository = lookupRepository;
        this.userRepository = userRepository;
        this.context = context;
    }

    public IncidentDTO getIncidentById(long id) throws IncidentServiceException {
        ModelMapper modelMapper = new ModelMapper();

        Incident incident = getIncident(id);
        log.info("Fetched the incident with id {}", id);
        return modelMapper.map(incident, IncidentDTO.class);
    }

    private Incident getIncident(long id) throws IncidentServiceException {
        Incident incident = incidentRepository.getById(id);
        if (incident == null) {
            String errorMessage = String.format("Cannot get an incident for the id %d", id);
            log.error(errorMessage);
            throw new IncidentServiceException(errorMessage);
        }
        return incident;
    }

    public void createIncident(IncidentRequest request) throws IncidentServiceException {
        Incident incident;
        String email = context.currentUser();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }
        String report = request.getPoliceReport();
        String raisedFor = request.getRaisedFor();
        log.info("raised for {} ", raisedFor);
        if (raisedFor.equals(RaisedFor.CLIENT.toString())
                && request.getClientId() != null
                && request.getHouseId() != null) {
            log.info("Creating the incident for CLIENT ");
            if (report.equals(YesNo.Yes.toString())) {

                incident =
                        Incident.builder()
                                .category(lookupRepository.getById(request.getCategoryId()))
                                .type(lookupRepository.getById(request.getTypeId()))
                                .classification(
                                        lookupRepository.getById(request.getClassificationId()))
                                .house(houseRepository.getById(request.getHouseId()))
                                .client(clientRepository.getById(request.getClientId()))
                                .status(Status.valueOf(request.getStatus()))
                                .raisedFor(RaisedFor.valueOf(request.getRaisedFor()))
                                .description(request.getDescription())
                                .dateOccurred(
                                        LocalDateTime.parse(
                                                request.getDateOccurred(),
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .createdAt(context.now())
                                .escalated(false)
                                .escalatedTo(null)
                                .location(request.getLocation())
                                .exactLocation(request.getExactLocation())
                                .reportedBy(user)
                                .closedBy("")
                                .reviewedBy("")
                                .injuredGivenName(request.getInjuredGivenName())
                                .injuredFamilyName(request.getInjuredFamilyName())
                                .witnessName(request.getWitnessName())
                                .witnessDesignation(request.getWitnessDesignation())
                                .followUpResponsibility(request.getFollowUpResponsibility())
                                .policeReport(YesNo.valueOf(request.getPoliceReport()))
                                .policeName(request.getPoliceName())
                                .policeNumber(request.getPoliceNumber())
                                .policeStation(request.getPoliceStation())
                                .beforeIncident(request.getBeforeIncident())
                                .immediateAction(request.getImmediateAction())
                                .reportableToNDIS(YesNo.valueOf(request.getReportableToNDIS()))
                                .reportableToWorksafe(
                                        YesNo.valueOf(request.getReportableToWorksafe()))
                                .build();
            } else {
                incident =
                        Incident.builder()
                                .category(lookupRepository.getById(request.getCategoryId()))
                                .type(lookupRepository.getById(request.getTypeId()))
                                .classification(
                                        lookupRepository.getById(request.getClassificationId()))
                                .house(houseRepository.getById(request.getHouseId()))
                                .client(clientRepository.getById(request.getClientId()))
                                .status(Status.valueOf(request.getStatus()))
                                .raisedFor(RaisedFor.valueOf(request.getRaisedFor()))
                                .description(request.getDescription())
                                .dateOccurred(
                                        LocalDateTime.parse(
                                                request.getDateOccurred(),
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .createdAt(context.now())
                                .escalated(false)
                                .escalatedTo(null)
                                .location(request.getLocation())
                                .exactLocation(request.getExactLocation())
                                .reportedBy(user)
                                .closedBy("")
                                .reviewedBy("")
                                .injuredGivenName(request.getInjuredGivenName())
                                .injuredFamilyName(request.getInjuredFamilyName())
                                .witnessName(request.getWitnessName())
                                .witnessDesignation(request.getWitnessDesignation())
                                .followUpResponsibility(request.getFollowUpResponsibility())
                                .policeReport(YesNo.valueOf(request.getPoliceReport()))
                                .beforeIncident(request.getBeforeIncident())
                                .immediateAction(request.getImmediateAction())
                                .reportableToNDIS(YesNo.valueOf(request.getReportableToNDIS()))
                                .reportableToWorksafe(
                                        YesNo.valueOf(request.getReportableToWorksafe()))
                                .build();
            }
        } else {
            log.info("Creating the incident for STAFF ");
            if (report.equals(YesNo.Yes.toString())) {
                incident =
                        Incident.builder()
                                .category(lookupRepository.getById(request.getCategoryId()))
                                .type(lookupRepository.getById(request.getTypeId()))
                                .classification(
                                        lookupRepository.getById(request.getClassificationId()))
                                .status(Status.valueOf(request.getStatus()))
                                .raisedFor(RaisedFor.valueOf(request.getRaisedFor()))
                                .description(request.getDescription())
                                .dateOccurred(
                                        LocalDateTime.parse(
                                                request.getDateOccurred(),
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .createdAt(context.now())
                                .escalated(false)
                                .escalatedTo(null)
                                .location(request.getLocation())
                                .exactLocation(request.getExactLocation())
                                .reportedBy(user)
                                .closedBy("")
                                .reviewedBy("")
                                .injuredGivenName(request.getInjuredGivenName())
                                .injuredFamilyName(request.getInjuredFamilyName())
                                .witnessName(request.getWitnessName())
                                .witnessDesignation(request.getWitnessDesignation())
                                .followUpResponsibility(request.getFollowUpResponsibility())
                                .policeReport(YesNo.valueOf(request.getPoliceReport()))
                                .policeName(request.getPoliceName())
                                .policeNumber(request.getPoliceNumber())
                                .policeStation(request.getPoliceStation())
                                .beforeIncident(request.getBeforeIncident())
                                .immediateAction(request.getImmediateAction())
                                .reportableToNDIS(YesNo.valueOf(request.getReportableToNDIS()))
                                .reportableToWorksafe(
                                        YesNo.valueOf(request.getReportableToWorksafe()))
                                .build();
            } else {
                incident =
                        Incident.builder()
                                .category(lookupRepository.getById(request.getCategoryId()))
                                .type(lookupRepository.getById(request.getTypeId()))
                                .classification(
                                        lookupRepository.getById(request.getClassificationId()))
                                .client(clientRepository.getById(request.getClientId()))
                                .status(Status.valueOf(request.getStatus()))
                                .raisedFor(RaisedFor.valueOf(request.getRaisedFor()))
                                .description(request.getDescription())
                                .dateOccurred(
                                        LocalDateTime.parse(
                                                request.getDateOccurred(),
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .createdAt(context.now())
                                .escalated(false)
                                .escalatedTo(null)
                                .location(request.getLocation())
                                .exactLocation(request.getExactLocation())
                                .reportedBy(user)
                                .closedBy("")
                                .reviewedBy("")
                                .injuredGivenName(request.getInjuredGivenName())
                                .injuredFamilyName(request.getInjuredFamilyName())
                                .witnessName(request.getWitnessName())
                                .witnessDesignation(request.getWitnessDesignation())
                                .followUpResponsibility(request.getFollowUpResponsibility())
                                .policeReport(YesNo.valueOf(request.getPoliceReport()))
                                .beforeIncident(request.getBeforeIncident())
                                .immediateAction(request.getImmediateAction())
                                .reportableToNDIS(YesNo.valueOf(request.getReportableToNDIS()))
                                .reportableToWorksafe(
                                        YesNo.valueOf(request.getReportableToWorksafe()))
                                .build();
            }
        }
        incidentRepository.save(incident);
        log.info("Created the incident with reportedBy {} ", incident.getReportedBy());
    }

    private void handleError(String message, String... params) throws IncidentServiceException {
        String errorMessage = String.format(message, params);
        log.error(errorMessage);
        throw new IncidentServiceException(errorMessage);
    }

    public List<IncidentDTO> listIncident(ListIncidentRequest request)
            throws IncidentServiceException {
        List<Incident> res;
        String email = context.currentUser();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }

        Pageable page =
                PageRequest.of(
                        request.getPageNumber(), request.getPageSize(), Sort.by("id").descending());
        log.info("request {} for list incidents is ", request);
        if (request.getHouseCode() != null && request.getClientName() != null) {
            log.info("Fetching the incidents based on the houseCode and clientName ");
            res =
                    incidentRepository.findAllByHouse_HouseCodeAndClient_Name(
                            request.getHouseCode(), request.getClientName(), page);
        } else if (request.getHouseCode() != null) {
            log.info("Fetching the incidents based on the houseCode ");
            res = incidentRepository.findAllByHouse_HouseCode(request.getHouseCode(), page);
        } else if (request.getClientName() != null) {
            log.info("Fetching the incidents based on the clientName");
            res = incidentRepository.findAllByClient_Name(request.getClientName(), page);
        } else if (request.getReportedBy() != null && request.getReportedBy() == true) {
            log.info("Fetching the incidents based on the reportedBy ");
            res = incidentRepository.findAllByReportedById(user.getId(), page);
        } else {
            log.info("Fetching all the incidents ");
            res = incidentRepository.findAll(page).getContent();
        }
        log.info("Fetched the incidents based on the request {}", request);
        return res.stream()
                .map(item -> new ModelMapper().map(item, IncidentDTO.class))
                .collect(Collectors.toList());
    }

    public void closeIncident(long id, String closedBy) {
        Optional<Incident> incident = incidentRepository.findById(id);

        if (incident.isPresent()) {
            Incident entity = incident.get();
            entity.setStatus(Status.CLOSED);
            entity.setClosedBy(closedBy);
            incidentRepository.save(entity);
            log.info("Closed the incident with id {}.", id);
        }
    }

    public void escalateIssue(long id, long userId) throws IncidentServiceException {
        Optional<Incident> incident = incidentRepository.findById(id);
        Optional<User> user = userRepository.findById(userId);
        if (incident.isPresent() && user.isPresent()) {
            Incident entity = incident.get();
            entity.setEscalated(true);
            entity.setEscalatedTo(user.get());
            incidentRepository.save(entity);
            log.info("Escalated the incident with id {} to user {}.", id, userId);
        } else {
            String errorMessage =
                    "Failed to escalate the incident, either the incident or user not present";
            log.error(errorMessage);
            throw new IncidentServiceException(errorMessage);
        }
    }

    public void updateIncident(Long id, IncidentRequest request) throws IncidentServiceException {
        Optional<Incident> incident = incidentRepository.findById(id);
        String raisedFor = request.getRaisedFor();
        String report = request.getPoliceReport();
        log.info("raised for {}", raisedFor);
        if (incident.isPresent()) {
            if (raisedFor.equals(RaisedFor.CLIENT.toString())) {
                log.info("updating for CLIENT");
                Incident existingEntity = incident.get();
                existingEntity.setCategory(lookupRepository.getById(request.getCategoryId()));
                existingEntity.setType(lookupRepository.getById(request.getTypeId()));
                existingEntity.setClassification(
                        lookupRepository.getById(request.getClassificationId()));
                existingEntity.setHouse(houseRepository.getById(request.getHouseId()));
                existingEntity.setClient(clientRepository.getById(request.getClientId()));
                existingEntity.setStatus(Status.valueOf(request.getStatus()));
                existingEntity.setRaisedFor(RaisedFor.valueOf(request.getRaisedFor()));
                existingEntity.setDescription(request.getDescription());
                existingEntity.setDateOccurred(
                        LocalDateTime.parse(
                                request.getDateOccurred(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                existingEntity.setLocation(request.getLocation());
                existingEntity.setExactLocation(request.getExactLocation());
                existingEntity.setInjuredGivenName(request.getInjuredGivenName());
                existingEntity.setInjuredFamilyName(request.getInjuredFamilyName());
                existingEntity.setWitnessName(request.getWitnessName());
                existingEntity.setWitnessDesignation(request.getWitnessDesignation());
                existingEntity.setFollowUpResponsibility(request.getFollowUpResponsibility());
                existingEntity.setPoliceReport(YesNo.valueOf(request.getPoliceReport()));
                if (report.equals(YesNo.Yes.toString())) {

                    existingEntity.setPoliceName(request.getPoliceName());
                    existingEntity.setPoliceNumber(request.getPoliceNumber());
                    existingEntity.setPoliceStation(request.getPoliceStation());
                }
                existingEntity.setBeforeIncident(request.getBeforeIncident());
                existingEntity.setImmediateAction(request.getImmediateAction());
                existingEntity.setReportableToNDIS(YesNo.valueOf(request.getReportableToNDIS()));
                existingEntity.setReportableToWorksafe(
                        YesNo.valueOf(request.getReportableToWorksafe()));
                incidentRepository.save(existingEntity);

            } else if (raisedFor.equals(RaisedFor.STAFF.toString())) {
                log.info("updating for STAFF");
                Incident existingEntity = incident.get();
                existingEntity.setCategory(lookupRepository.getById(request.getCategoryId()));
                existingEntity.setType(lookupRepository.getById(request.getTypeId()));
                existingEntity.setClassification(
                        lookupRepository.getById(request.getClassificationId()));

                existingEntity.setRaisedFor(RaisedFor.valueOf(request.getRaisedFor()));
                existingEntity.setStatus(Status.valueOf(request.getStatus()));
                existingEntity.setDescription(request.getDescription());
                existingEntity.setDateOccurred(
                        LocalDateTime.parse(
                                request.getDateOccurred(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                existingEntity.setLocation(request.getLocation());
                existingEntity.setExactLocation(request.getExactLocation());
                existingEntity.setInjuredGivenName(request.getInjuredGivenName());
                existingEntity.setInjuredFamilyName(request.getInjuredFamilyName());
                existingEntity.setWitnessName(request.getWitnessName());
                existingEntity.setWitnessDesignation(request.getWitnessDesignation());
                existingEntity.setFollowUpResponsibility(request.getFollowUpResponsibility());
                existingEntity.setPoliceReport(YesNo.valueOf(request.getPoliceReport()));
                if (report.equals(YesNo.Yes.toString())) {
                    existingEntity.setPoliceName(request.getPoliceName());
                    existingEntity.setPoliceNumber(request.getPoliceNumber());
                    existingEntity.setPoliceStation(request.getPoliceStation());
                }
                existingEntity.setBeforeIncident(request.getBeforeIncident());
                existingEntity.setImmediateAction(request.getImmediateAction());
                existingEntity.setReportableToNDIS(YesNo.valueOf(request.getReportableToNDIS()));
                existingEntity.setReportableToWorksafe(
                        YesNo.valueOf(request.getReportableToWorksafe()));
                incidentRepository.save(existingEntity);
            }
            log.info("Updated the incident for the given id {}", request.getId());
        } else {
            String errorMessage = String.format("Incident not found %d", request.getId());
            log.error(errorMessage);
            throw new IncidentServiceException(errorMessage);
        }
    }

    public List<ListGraphDTO> listForGraph(ListForGraphRequest request) {
        log.info("Fetching list incidents based on request ");
        List<ListGraphDTO> res =
                incidentRepository
                        .findAllByRaisedForAndCreatedAtBetweenAndStatusNot(
                                RaisedFor.valueOf(request.getRaisedFor()),
                                request.getStart(),
                                request.getEnd(),
                                Status.INACTIVE)
                        .stream()
                        .map(item -> new ModelMapper().map(item, ListGraphDTO.class))
                        .collect(Collectors.toList());

        log.info("Fetched the list incidents for graph ");
        return res;
    }
}
