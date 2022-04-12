package com.onedoorway.project.controller;

import com.onedoorway.project.dto.IncidentReviewDTO;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.ReviewServiceException;
import com.onedoorway.project.services.IncidentReviewService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/incident-review", produces = "application/json")
public class IncidentReviewController {
    private final IncidentReviewService incidentReviewService;

    public IncidentReviewController(@Autowired IncidentReviewService incidentReviewService) {
        this.incidentReviewService = incidentReviewService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Response> createIncidentReview(
            @Valid @RequestBody IncidentReviewDTO request) {
        try {
            incidentReviewService.createIncidentReview(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ReviewServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Duplicate review").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/{incidentId}")
    public ResponseEntity<List<IncidentReviewDTO>> getReviewById(@PathVariable long incidentId) {
        return new ResponseEntity<>(incidentReviewService.getReviewById(incidentId), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateReview(
            @PathVariable Long id, @Valid @RequestBody IncidentReviewDTO request) {
        try {
            incidentReviewService.updateReview(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ReviewServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).build(), HttpStatus.BAD_REQUEST);
        }
    }
}
