package com.onedoorway.project.controller;

import com.onedoorway.project.dto.GetHouseImageRequest;
import com.onedoorway.project.dto.HouseImageDTO;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.HouseImageServiceException;
import com.onedoorway.project.services.HouseImageService;
import java.security.InvalidKeyException;
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
public class HouseImageController {

    private final HouseImageService houseImageService;

    public HouseImageController(@Autowired HouseImageService houseImageService) {
        this.houseImageService = houseImageService;
    }

    @SneakyThrows
    @PostMapping(
            value = "/upload-image",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadImage(
            @RequestPart("houseCode") String houseCode,
            @RequestPart("lastUploadedBy") String lastUploadedBy,
            @RequestPart("file") MultipartFile file) {
        try {
            houseImageService.storeImage(
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType(),
                    houseCode,
                    lastUploadedBy);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseImageServiceException e) {
            log.error("Error for image upload {}", e.getMessage());
            return new ResponseEntity<>(
                    Response.builder().message(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(value = "/get-image", produces = "application/json")
    public ResponseEntity<?> getImage(@Valid @RequestBody GetHouseImageRequest request) {
        try {
            return new ResponseEntity<>(
                    HouseImageDTO.builder()
                            .imageUrl(houseImageService.getImageUrl(request))
                            .build(),
                    HttpStatus.OK);
        } catch (InvalidKeyException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Cannot get Url").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
