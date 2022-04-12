package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.DailyNoteServiceException;
import com.onedoorway.project.services.DailyNoteService;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping(path = "/dailynote", produces = "application/json")
public class DailyNoteController {
    private final DailyNoteService dailyNoteService;

    public DailyNoteController(@Autowired DailyNoteService dailyNoteService) {
        this.dailyNoteService = dailyNoteService;
    }

    @SneakyThrows
    @PostMapping(path = "/create")
    public ResponseEntity<Response> createNote(@RequestBody DailyNoteRequest request) {
        dailyNoteService.createDailyNote(request);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/get/{houseCode}")
    public ResponseEntity<List<DailyNoteDTO>> getDailyNotesByHouseCode(
            @PathVariable String houseCode) {
        return new ResponseEntity<>(dailyNoteService.getNotesByCode(houseCode), HttpStatus.OK);
    }

    @GetMapping("/get/{userId}/{houseCode}")
    public ResponseEntity<List<DailyNoteDTO>> getDailyNotesByUserAndHouseCode(
            @PathVariable long userId, @PathVariable String houseCode) {
        return new ResponseEntity<>(
                dailyNoteService.getDailyNotes(houseCode, userId), HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<DailyNoteDTO>> searchDailyNotes(
            @RequestBody SearchDailyNoteRequest request) {
        return new ResponseEntity<>(dailyNoteService.search(request), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteNotes(@PathVariable long id) {
        dailyNoteService.deleteDailyNote(id);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateNote(@RequestBody UpdateDailyNoteRequest request) {
        try {
            dailyNoteService.updateDailyNote(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (DailyNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Daily Note not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
