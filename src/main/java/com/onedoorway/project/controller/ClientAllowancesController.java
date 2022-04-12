package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.dto.ClientAllowancesRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.ClientAllowancesServiceException;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.services.ClientAllowancesService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/client-allowances", produces = "application/json")
public class ClientAllowancesController {
    private final ClientAllowancesService clientAllowancesService;

    public ClientAllowancesController(@Autowired ClientAllowancesService clientAllowancesService) {
        this.clientAllowancesService = clientAllowancesService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createClientAllowances(
            @Valid @RequestBody ClientAllowancesRequest request) {
        try {
            clientAllowancesService.createClientAllowances(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientAllowancesServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create client allowance")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/list/{clientId}")
    public ResponseEntity<?> getClientAllowances(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(
                    clientAllowancesService.getClientAllowancesById(clientId), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Error in fetching Client Allowances")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateClientAllowances(
            @PathVariable Long id, @Valid @RequestBody UpdateClientAllowancesRequest request) {
        try {
            clientAllowancesService.updateClientAllowances(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientAllowancesServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("client allowances not found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
