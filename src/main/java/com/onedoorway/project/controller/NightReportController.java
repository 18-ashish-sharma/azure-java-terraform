package com.onedoorway.project.controller;

import com.onedoorway.project.dto.NightReportRequest;
import com.onedoorway.project.dto.ParticularNightReportRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.NightReportServiceException;
import com.onedoorway.project.services.NightReportService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/night-report", produces = "application/json")
public class NightReportController {

    private final NightReportService nightReportService;

    public NightReportController(@Autowired NightReportService nightReportService) {
        this.nightReportService = nightReportService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createNightReport(
            @Valid @RequestBody NightReportRequest request) {
        try {
            nightReportService.createNightReport(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (NightReportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Client not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateReport(
            @PathVariable Long id, @Valid @RequestBody NightReportRequest request) {
        try {
            nightReportService.updateReport(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (NightReportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("report not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/{clientId}")
    public ResponseEntity<?> getNightReport(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(
                    nightReportService.getNightReportById(clientId), HttpStatus.OK);
        } catch (NightReportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("NightReport could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/get-particular")
    public ResponseEntity<?> getParticularNightReport(
            @Valid @RequestBody ParticularNightReportRequest request) {
        try {
            return new ResponseEntity<>(
                    nightReportService.getParticularNightReport(request), HttpStatus.OK);
        } catch (NightReportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("NightReport could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
