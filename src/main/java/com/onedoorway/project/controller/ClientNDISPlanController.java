package com.onedoorway.project.controller;

import com.onedoorway.project.dto.ClientNDISPlanRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.UpdateClientNDISPlanRequest;
import com.onedoorway.project.exception.ClientNDISPlanServiceException;
import com.onedoorway.project.services.ClientNDISPlanService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping(path = "/client-ndis-plan", produces = "application/json")
public class ClientNDISPlanController {
    private final ClientNDISPlanService clientNDISPlanService;

    public ClientNDISPlanController(@Autowired ClientNDISPlanService clientNDISPlanService) {
        this.clientNDISPlanService = clientNDISPlanService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public ResponseEntity<Response> createClientNDISPlan(
            @Valid @RequestBody ClientNDISPlanRequest request) {
        try {
            clientNDISPlanService.createClientNDISPlan(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientNDISPlanServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create client ndis plan")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("list/{clientId}")
    public ResponseEntity<?> getClientNDISPlan(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(
                    clientNDISPlanService.getClientNDISPlan(clientId), HttpStatus.OK);
        } catch (ClientNDISPlanServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("client ndis plan could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClientNDISPlan(
            @PathVariable Long id, @Valid @RequestBody UpdateClientNDISPlanRequest request) {
        try {
            clientNDISPlanService.updateClientNDISPlan(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientNDISPlanServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("client ndis plan not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(
            value = "/upload-support/{clientNDISPlanId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadDocument(
            @RequestPart("clientName") String clientName,
            @RequestPart("clientNDISPlanId") long clientNDISPlanId,
            @RequestPart("file") MultipartFile file) {
        try {
            clientNDISPlanService.storeDocument(
                    file.getInputStream(),
                    file.getSize(),
                    clientNDISPlanId,
                    clientName,
                    file.getContentType());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientNDISPlanServiceException e) {
            log.error("Invalid stream for document upload {}", e.getMessage());
            return new ResponseEntity<>(
                    Response.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(
            value = "/upload-other/{clientNDISPlanId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadOtherDocument(
            @RequestPart("clientName") String clientName,
            @RequestPart("clientNDISPlanId") long clientNDISPlanId,
            @RequestPart("file") MultipartFile file) {
        try {
            clientNDISPlanService.storeOtherDocument(
                    file.getInputStream(),
                    file.getSize(),
                    clientNDISPlanId,
                    clientName,
                    file.getContentType());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientNDISPlanServiceException e) {
            log.error("Invalid stream for document upload {}", e.getMessage());
            return new ResponseEntity<>(
                    Response.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }
}
