package com.onedoorway.project.controller;

import com.onedoorway.project.dto.PowerOfAttorneyRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.UpdatePowerOfAttorneyRequest;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.exception.PowerOfAttorneyServiceException;
import com.onedoorway.project.services.PowerOfAttorneyService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/power-of-attorney", produces = "application/json")
public class PowerOfAttorneyController {

    private final PowerOfAttorneyService powerOfAttorneyService;

    public PowerOfAttorneyController(@Autowired PowerOfAttorneyService powerOfAttorneyService) {
        this.powerOfAttorneyService = powerOfAttorneyService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createPowerOfAttorney(
            @Valid @RequestBody PowerOfAttorneyRequest request) {
        try {
            powerOfAttorneyService.createPowerOfAttorney(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (PowerOfAttorneyServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create  power of attorney")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/list/{clientId}")
    public ResponseEntity<?> listPowerOfAttorney(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(
                    powerOfAttorneyService.listPowerOfAttorney(clientId), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Error in fetching power of attorney")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updatePowerOfAttorney(
            @PathVariable Long id, @Valid @RequestBody UpdatePowerOfAttorneyRequest request) {
        try {
            powerOfAttorneyService.updatePowerOfAttorney(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (PowerOfAttorneyServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("power of attorney could not be updated")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
