package com.onedoorway.project.controller;

import com.onedoorway.project.dto.BowelNoteRequest;
import com.onedoorway.project.dto.ListBowelNoteRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.UpdateBowelNoteRequest;
import com.onedoorway.project.exception.BowelNoteServiceException;
import com.onedoorway.project.services.BowelNoteService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/bowel-note", produces = "application/json")
public class BowelNoteController {
    private final BowelNoteService bowelNoteService;

    public BowelNoteController(@Autowired BowelNoteService bowelNoteService) {
        this.bowelNoteService = bowelNoteService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createBowelNote(@Valid @RequestBody BowelNoteRequest request) {
        try {
            bowelNoteService.createBowelNote(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (BowelNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create bowel note")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/list")
    public ResponseEntity<?> listBowelNote(@RequestBody ListBowelNoteRequest request) {
        try {
            return new ResponseEntity<>(bowelNoteService.listBowelNotes(request), HttpStatus.OK);
        } catch (BowelNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("bowelNote could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateBowelNote(
            @PathVariable Long id, @Valid @RequestBody UpdateBowelNoteRequest request) {
        try {
            bowelNoteService.updateBowelNote(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (BowelNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("bowel note not found or outdated")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
