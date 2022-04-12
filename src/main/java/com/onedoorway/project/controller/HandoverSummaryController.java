package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.HandoverSummaryServiceException;
import com.onedoorway.project.services.HandoverSummaryService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/handover", produces = "application/json")
public class HandoverSummaryController {
    private final HandoverSummaryService handoverSummaryService;

    public HandoverSummaryController(@Autowired HandoverSummaryService handoverSummaryService) {
        this.handoverSummaryService = handoverSummaryService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createHandoverSummary(
            @Valid @RequestBody HandoverSummaryRequest request) {
        try {
            handoverSummaryService.createHandoverSummary(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HandoverSummaryServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create hand over summary")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/list")
    public ResponseEntity<List<HandoverSummaryDTO>> listHandoverSummary(
            @RequestBody ListHandoverSummaryRequest request) {
        return new ResponseEntity<>(
                handoverSummaryService.listHandoverSummary(request), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> UpdateHandoverSummary(
            @PathVariable Long id, @Valid @RequestBody UpdateHandoverSummaryRequest request) {
        try {
            handoverSummaryService.updateHandoverSummary(id, request);

            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HandoverSummaryServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("handover summary not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
