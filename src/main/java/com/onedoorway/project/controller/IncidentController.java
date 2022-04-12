package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.IncidentServiceException;
import com.onedoorway.project.services.IncidentService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/incident", produces = "application/json")
public class IncidentController {
    private final IncidentService incidentService;

    public IncidentController(@Autowired IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createIncident(@Valid @RequestBody IncidentRequest request) {
        incidentService.createIncident(request);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/get/{id}")
    public ResponseEntity<IncidentDTO> getIncident(@PathVariable long id) {
        return new ResponseEntity<>(incidentService.getIncidentById(id), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/list")
    public ResponseEntity<List<IncidentDTO>> listIncidents(
            @RequestBody ListIncidentRequest request) {
        return new ResponseEntity<>(incidentService.listIncident(request), HttpStatus.OK);
    }

    @PatchMapping("/close/{id}/{closedBy}")
    public ResponseEntity<Response> closeIncident(
            @PathVariable long id, @PathVariable String closedBy) {
        incidentService.closeIncident(id, closedBy);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @SneakyThrows
    @PatchMapping("/escalate/{id}/{userId}")
    public ResponseEntity<Response> closeIncident(
            @PathVariable long id, @PathVariable long userId) {
        incidentService.escalateIssue(id, userId);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateIncident(
            @PathVariable Long id, @Valid @RequestBody IncidentRequest request) {
        try {
            incidentService.updateIncident(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (IncidentServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/listForGraph")
    public ResponseEntity<List<ListGraphDTO>> listForGraph(
            @Valid @RequestBody ListForGraphRequest request) {
        return new ResponseEntity<>(incidentService.listForGraph(request), HttpStatus.OK);
    }
}
