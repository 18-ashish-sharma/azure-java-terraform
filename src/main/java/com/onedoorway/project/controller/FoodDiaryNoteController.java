package com.onedoorway.project.controller;

import com.onedoorway.project.dto.FoodDiaryNoteRequest;
import com.onedoorway.project.dto.ParticularFoodDiaryNoteRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.FoodDiaryNoteServiceException;
import com.onedoorway.project.services.FoodDiaryNoteService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/food-diary", produces = "application/json")
public class FoodDiaryNoteController {
    private final FoodDiaryNoteService foodDiaryNoteService;

    public FoodDiaryNoteController(@Autowired FoodDiaryNoteService foodDiaryNoteService) {
        this.foodDiaryNoteService = foodDiaryNoteService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createFoodReport(
            @Valid @RequestBody FoodDiaryNoteRequest request) {
        try {
            foodDiaryNoteService.createFoodReport(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (FoodDiaryNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create food diary")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/{clientId}/{mealType}")
    public ResponseEntity<?> getFoodDiaryNote(
            @PathVariable long clientId, @PathVariable String mealType) {
        try {
            return new ResponseEntity<>(
                    foodDiaryNoteService.getFoodDiaryNote(clientId, mealType), HttpStatus.OK);
        } catch (FoodDiaryNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("FoodDiaryNote could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateFoodDiaryNote(
            @PathVariable Long id, @Valid @RequestBody FoodDiaryNoteRequest request) {
        try {
            foodDiaryNoteService.updateFoodDiaryNote(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (FoodDiaryNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("report not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/get-particular")
    public ResponseEntity<?> getParticularFoodDiaryNote(
            @Valid @RequestBody ParticularFoodDiaryNoteRequest request) {
        try {
            return new ResponseEntity<>(
                    foodDiaryNoteService.getParticularFoodDiaryNote(request), HttpStatus.OK);
        } catch (FoodDiaryNoteServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("FoodDiaryNote could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
