package com.onedoorway.project.controller;

import com.onedoorway.project.dto.ClientTransportRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.UpdateClientTransportRequest;
import com.onedoorway.project.exception.ClientTransportServiceException;
import com.onedoorway.project.services.ClientTransportService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/client-transport", produces = "application/json")
public class ClientTransportController {
    private final ClientTransportService clientTransportService;

    public ClientTransportController(@Autowired ClientTransportService clientTransportService) {
        this.clientTransportService = clientTransportService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createClientTransport(
            @Valid @RequestBody ClientTransportRequest request) {
        try {
            clientTransportService.createClientTransport(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientTransportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create client transport")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/list/{clientId}")
    public ResponseEntity<?> listClientTransport(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(
                    clientTransportService.getClientTransport(clientId), HttpStatus.OK);
        } catch (ClientTransportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("client transport not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateClientTransport(
            @PathVariable Long id, @Valid @RequestBody UpdateClientTransportRequest request) {
        try {
            clientTransportService.updateClientTransport(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientTransportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("client transport not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
