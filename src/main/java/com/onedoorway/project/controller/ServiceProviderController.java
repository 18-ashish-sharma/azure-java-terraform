package com.onedoorway.project.controller;

import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.ServiceProviderRequest;
import com.onedoorway.project.dto.UpdateServiceProviderRequest;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.exception.ServiceProviderException;
import com.onedoorway.project.services.ServiceProviderService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/service-provider", produces = "application/json")
public class ServiceProviderController {
    private final ServiceProviderService serviceProviderService;

    public ServiceProviderController(@Autowired ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @SneakyThrows
    @PostMapping("/service-provider/create")
    public ResponseEntity<?> createServiceProvider(
            @Valid @RequestBody ServiceProviderRequest request) {
        try {
            serviceProviderService.createServiceProvider(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ServiceProviderException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("could not create service provider")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/list/{clientId}")
    public ResponseEntity<?> getServiceProvider(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(
                    serviceProviderService.getServiceProviderById(clientId), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Error in fetching Service provider")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateServiceProvider(
            @PathVariable Long id, @Valid @RequestBody UpdateServiceProviderRequest request) {
        try {
            serviceProviderService.updateServiceProvider(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ServiceProviderException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("service provider not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
