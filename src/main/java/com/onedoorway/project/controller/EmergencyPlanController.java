package com.onedoorway.project.controller;

import com.onedoorway.project.dto.GetEmergencyPlanRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.EmergencyPlanServiceException;
import com.onedoorway.project.services.EmergencyPlanService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Log4j2
public class EmergencyPlanController {

    private final EmergencyPlanService emergencyPlanService;

    public EmergencyPlanController(@Autowired EmergencyPlanService emergencyPlanService) {
        this.emergencyPlanService = emergencyPlanService;
    }

    @SneakyThrows
    @PostMapping(
            value = "/upload",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadFile(
            @RequestPart("houseCode") String houseCode,
            @RequestPart("fileType") String fileType,
            @RequestPart("file") MultipartFile file) {
        try {
            emergencyPlanService.storeFile(
                    file.getInputStream(), file.getSize(), houseCode, fileType);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (EmergencyPlanServiceException e) {
            log.error("Error occurred while uploading plan {}", e.getMessage());
            return new ResponseEntity<>(
                    Response.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(value = "/url/get", produces = "application/json")
    public ResponseEntity<?> getPlanUrl(@Valid @RequestBody GetEmergencyPlanRequest request) {
        try {
            return new ResponseEntity<>(emergencyPlanService.getPlanUrl(request), HttpStatus.OK);
        } catch (EmergencyPlanServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Emergency plan could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
