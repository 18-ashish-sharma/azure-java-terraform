package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.CaseNoteServiceException;
import com.onedoorway.project.services.CaseNoteService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/case-note", produces = "application/json")
public class CaseNoteController {
    private final CaseNoteService caseNoteService;

    public CaseNoteController(@Autowired CaseNoteService caseNoteService) {
        this.caseNoteService = caseNoteService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createCaseNote(@Valid @RequestBody CaseNoteRequest request) {
        try {
            caseNoteService.createCaseNote(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (CaseNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("could not create case note").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getCaseNote(@PathVariable long id) {
        try {
            return new ResponseEntity<>(caseNoteService.getCaseNote(id), HttpStatus.OK);
        } catch (CaseNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Case note could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/list")
    public ResponseEntity<List<CaseNoteDTO>> listCaseNotes(
            @RequestBody ListCaseNoteRequest request) {
        return new ResponseEntity<>(caseNoteService.listCaseNotes(request), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateCaseNote(
            @PathVariable Long id, @Valid @RequestBody UpdateCaseNoteRequest request) {
        try {
            caseNoteService.updateCaseNote(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (CaseNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("case note not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
