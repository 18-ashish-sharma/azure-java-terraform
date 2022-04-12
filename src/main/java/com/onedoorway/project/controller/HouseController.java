package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.HouseServiceException;
import com.onedoorway.project.services.HouseService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/house", produces = "application/json")
public class HouseController {
    private final HouseService houseService;

    public HouseController(@Autowired HouseService houseService) {
        this.houseService = houseService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createHouse(@Valid @RequestBody AddHouseRequest request) {
        try {
            houseService.createHouse(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/get")
    public ResponseEntity<HouseDTO> getHouse(@RequestBody GetHouseByCodeRequest request) {
        return new ResponseEntity<>(houseService.getHouseByCode(request), HttpStatus.OK);
    }

    @PostMapping("/add-client")
    public ResponseEntity<Response> addClient(@Valid @RequestBody AddClientRequest request) {
        try {
            houseService.addClient(request.getClientId(), request.getHouseCode());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("House not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/fetch-clients")
    public ResponseEntity<List<ClientDTO>> getClientsByHouse(
            @RequestBody GetClientsByHouseRequest request) {
        return new ResponseEntity<>(houseService.getClientsByHouse(request), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/add-user")
    public ResponseEntity<Response> addUser(@Valid @RequestBody UserToHouseRequest request) {
        try {
            houseService.mapHouseToUser(request.getUserId(), request.getHouseCode());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove-client")
    public ResponseEntity<Response> removeClient(@Valid @RequestBody RemoveClientRequest request) {
        try {
            houseService.removeClient(request.getHouseCode(), request.getClientId());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("remove not successful").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<HouseDTO>> listHouses() {
        return new ResponseEntity<>(houseService.listAllHouses(), HttpStatus.OK);
    }

    @PutMapping("/update/{houseId}")
    public ResponseEntity<Response> updateHouses(
            @Valid @RequestBody AddHouseRequest request, @PathVariable long houseId) {
        try {
            houseService.updateHouse(request, houseId);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping("/create-contact")
    public ResponseEntity<Response> createHouseContact(
            @Valid @RequestBody HouseContactRequest request) {
        try {
            houseService.createHouseContact(request);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("house contact created").build(),
                    HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("cannot create houseContact").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/contact/{id}")
    public ResponseEntity<?> getHouseContact(@PathVariable long id) {
        try {
            return new ResponseEntity<>(houseService.getHouseContactById(id), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("HouseContact could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/contact/{id}")
    public ResponseEntity<Response> updateHouseContact(
            @PathVariable Long id, @Valid @RequestBody UpdateHouseContactRequest request) {
        try {
            houseService.updateHouseContact(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (HouseServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("houseContact not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(value = "/list/contact")
    public ResponseEntity<List<HouseContactDTO>> ListHouseContacts(
            @RequestBody ListHouseContactRequest request) {
        return new ResponseEntity<>(houseService.listHouseContacts(request), HttpStatus.OK);
    }
}
