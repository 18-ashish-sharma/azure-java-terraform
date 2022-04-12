package com.onedoorway.project.services;

import com.onedoorway.project.dto.IncidentReviewDTO;
import com.onedoorway.project.exception.ReviewServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.IncidentRepository;
import com.onedoorway.project.repository.IncidentReviewRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class IncidentReviewService {
    private final IncidentReviewRepository incidentReviewRepository;
    private final IncidentRepository incidentRepository;

    @Autowired
    public IncidentReviewService(
            IncidentReviewRepository incidentReviewRepository,
            IncidentRepository incidentRepository) {
        this.incidentReviewRepository = incidentReviewRepository;
        this.incidentRepository = incidentRepository;
    }

    public void createIncidentReview(IncidentReviewDTO request) throws ReviewServiceException {
        if (incidentReviewRepository.getByIncident_Id(request.getIncidentId()) != null) {
            throw new ReviewServiceException("Review with the same incident id already exists");
        }
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(
                new PropertyMap<IncidentReviewDTO, IncidentReview>() {
                    @Override
                    protected void configure() {
                        skip(destination.getIncident());
                    }
                });
        IncidentReview incidentReview = modelMapper.map(request, IncidentReview.class);
        Incident incident = incidentRepository.getById(request.getIncidentId());
        incidentReview.setIncident(incident);
        incidentReview.setDueDate(
                LocalDate.parse(request.getDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        incidentReview.setCodeBreach(YesNo.valueOf(request.getCodeBreach()));
        incidentReview.setSupportPlanUpdate(YesNo.valueOf(request.getSupportPlanUpdate()));
        incidentReview.setFurtherSupport(YesNo.valueOf(request.getFurtherSupport()));
        incidentReviewRepository.save(incidentReview);
        incident.setReviewedBy(request.getReviewedBy());
        incident.setStatus(Status.REVIEWED);
        incidentRepository.save(incident);
    }

    public List<IncidentReviewDTO> getReviewById(long incidentId) throws ReviewServiceException {

        List<IncidentReviewDTO> res =
                incidentReviewRepository.findByIncident_Id(incidentId).stream()
                        .map(item -> new ModelMapper().map(item, IncidentReviewDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched the reviews based on incidentId {}", incidentId);
        return res;
    }

    public void updateReview(Long id, IncidentReviewDTO request) throws ReviewServiceException {
        Optional<IncidentReview> incidentReview = incidentReviewRepository.findById(id);
        if (incidentReview.isPresent()) {
            IncidentReview existingEntity = incidentReview.get();
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.addMappings(
                    new PropertyMap<IncidentReviewDTO, IncidentReview>() {
                        @Override
                        protected void configure() {
                            skip(destination.getIncident());
                        }
                    });
            mapper.map(request, existingEntity);
            existingEntity.setDueDate(
                    LocalDate.parse(
                            request.getDueDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            existingEntity.setCodeBreach(YesNo.valueOf(request.getCodeBreach()));
            existingEntity.setSupportPlanUpdate(YesNo.valueOf(request.getSupportPlanUpdate()));
            existingEntity.setFurtherSupport(YesNo.valueOf(request.getFurtherSupport()));
            existingEntity.getIncident().setReviewedBy(request.getReviewedBy());
            existingEntity.getIncident().setStatus(Status.REVIEWED);
            Incident incident = incidentRepository.getById(id);
            incidentRepository.save(incident);
            incidentReviewRepository.save(existingEntity);
            log.info("Updated the review for the given id {}", request.getId());
        } else {
            String errorMessage =
                    String.format("Cannot get a review for the id %d", request.getId());
            log.error(errorMessage);
            throw new ReviewServiceException(errorMessage);
        }
    }
}
