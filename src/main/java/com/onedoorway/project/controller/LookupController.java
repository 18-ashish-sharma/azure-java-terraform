package com.onedoorway.project.controller;

import static com.onedoorway.project.model.LookupType.*;

import com.onedoorway.project.dto.LookupDTO;
import com.onedoorway.project.services.LookupService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/lookup", produces = "application/json")
public class LookupController {

    private final LookupService lookupService;

    public LookupController(@Autowired LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @GetMapping(value = "/house-factors")
    public ResponseEntity<List<LookupDTO>> listHouseFactors() {
        return new ResponseEntity<>(
                lookupService.listLookups(INCIDENT_HOUSE_ASSET_FACTOR), HttpStatus.OK);
    }

    @GetMapping(value = "/classifications")
    public ResponseEntity<List<LookupDTO>> listClassifications() {
        return new ResponseEntity<>(
                lookupService.listLookups(INCIDENT_CLASSIFICATION), HttpStatus.OK);
    }

    @GetMapping(value = "/environment-factors")
    public ResponseEntity<List<LookupDTO>> listEnvironmentFactors() {
        return new ResponseEntity<>(
                lookupService.listLookups(INCIDENT_ENVIRONMENT_FACTOR), HttpStatus.OK);
    }

    @GetMapping(value = "/person-factors")
    public ResponseEntity<List<LookupDTO>> listPersonFactors() {
        return new ResponseEntity<>(
                lookupService.listLookups(INCIDENT_PERSON_FACTOR), HttpStatus.OK);
    }

    @GetMapping(value = "/categories")
    public ResponseEntity<List<LookupDTO>> listCategories() {
        return new ResponseEntity<>(lookupService.listLookups(INCIDENT_CATEGORY), HttpStatus.OK);
    }

    @GetMapping(value = "/types")
    public ResponseEntity<List<LookupDTO>> listTypes() {
        return new ResponseEntity<>(lookupService.listLookups(INCIDENT_TYPE), HttpStatus.OK);
    }

    @GetMapping(value = "/reports")
    public ResponseEntity<List<LookupDTO>> listReports() {
        return new ResponseEntity<>(lookupService.listLookups(REPORTS), HttpStatus.OK);
    }

    @GetMapping(value = "/nightly-tasks")
    public ResponseEntity<List<LookupDTO>> listNightlyTasks() {
        return new ResponseEntity<>(lookupService.listLookups(NIGHTLY_TASKS), HttpStatus.OK);
    }

    @GetMapping(value = "/list-note-categories")
    public ResponseEntity<List<LookupDTO>> listMiscellaneousNotes() {
        return new ResponseEntity<>(lookupService.listLookups(MISCELLANEOUS_NOTES), HttpStatus.OK);
    }
}
