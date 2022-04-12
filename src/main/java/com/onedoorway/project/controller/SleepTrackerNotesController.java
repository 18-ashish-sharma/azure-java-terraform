package com.onedoorway.project.controller;

import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.SleepTrackerNotesRequest;
import com.onedoorway.project.dto.UpdateSleepTrackerRequest;
import com.onedoorway.project.exception.SleepTrackerNotesServiceException;
import com.onedoorway.project.services.SleepTrackerNotesService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sleep-notes", produces = "application/json")
public class SleepTrackerNotesController {
    private final SleepTrackerNotesService sleepTrackerNotesService;

    public SleepTrackerNotesController(
            @Autowired SleepTrackerNotesService sleepTrackerNotesService) {
        this.sleepTrackerNotesService = sleepTrackerNotesService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createSleepTrackerNotes(
            @Valid @RequestBody SleepTrackerNotesRequest request) {
        try {
            sleepTrackerNotesService.createSleepTrackerNotes(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (SleepTrackerNotesServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("SleepTrackerNotes could not be created")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/{clientId}/{reportDate}")
    public ResponseEntity<?> getSleepTrackerNotes(
            @PathVariable long clientId, @PathVariable String reportDate) {
        try {
            return new ResponseEntity<>(
                    sleepTrackerNotesService.getSleepTrackerNote(clientId, reportDate),
                    HttpStatus.OK);
        } catch (SleepTrackerNotesServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("SleepTrackerNote could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateSleepTracker(
            @PathVariable Long id, @Valid @RequestBody UpdateSleepTrackerRequest request) {
        try {
            sleepTrackerNotesService.updateSleepTracker(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (SleepTrackerNotesServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("tracker notes not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
