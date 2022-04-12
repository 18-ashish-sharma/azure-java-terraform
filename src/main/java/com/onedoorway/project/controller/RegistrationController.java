package com.onedoorway.project.controller;

import com.onedoorway.project.dto.ChangePasswordRequest;
import com.onedoorway.project.dto.RegisterRequest;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.UserServiceException;
import com.onedoorway.project.services.UserService;
import javax.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @SneakyThrows
    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<Response> register(@Valid @RequestBody RegisterRequest register) {
        try {
            userService.register(
                    register.getEmail(),
                    register.getPassword(),
                    register.getFirstName(),
                    register.getLastName(),
                    register.getPhone(),
                    register.getMobile());
            log.info("Registered the user with email {}", register.getEmail());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (UserServiceException e) {
            log.error("Duplicate registration for user with email {}", register.getEmail());
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/change-password", produces = "application/json")
    public ResponseEntity<Response> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.updatePassword(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
