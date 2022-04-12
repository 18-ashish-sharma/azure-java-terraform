package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.MiscellaneousNoteServiceException;
import com.onedoorway.project.services.MiscellaneousNoteService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/miscellaneous-note", produces = "application/json")
public class MiscellaneousNoteController {
    private final MiscellaneousNoteService miscellaneousNoteService;

    public MiscellaneousNoteController(
            @Autowired MiscellaneousNoteService miscellaneousNoteService) {
        this.miscellaneousNoteService = miscellaneousNoteService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createMiscellaneousNote(
            @Valid @RequestBody MiscellaneousNoteRequest request) {
        try {
            miscellaneousNoteService.createMiscellaneousNote(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (MiscellaneousNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create miscellaneous note")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateMiscellaneousNote(
            @PathVariable Long id, @Valid @RequestBody UpdateMiscellaneousNoteRequest request) {
        try {
            miscellaneousNoteService.updateMiscellaneousNote(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (MiscellaneousNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Miscellaneous note not found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/list")
    public ResponseEntity<List<MiscellaneousNoteDTO>> listMiscellaneousNote(
            @RequestBody ListMiscellaneousNoteRequest request) {
        return new ResponseEntity<>(
                miscellaneousNoteService.listMiscellaneousNotes(request), HttpStatus.OK);
    }
}
