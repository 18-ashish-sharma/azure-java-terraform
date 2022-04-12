package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.UserServiceException;
import com.onedoorway.project.services.UserService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user", produces = "application/json")
public class UserController {
    private final UserService userService;

    public UserController(@Autowired UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserShortDTO>> listAllUsers() {
        return new ResponseEntity<>(userService.listAllUsers(), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/list-page")
    public ResponseEntity<ListUsersByPageResponse> listAllUsersByPage(
            @RequestBody ListUsersByPageRequest request) {
        ListUsersByPageResponse listUsersByPageResponse =
                ListUsersByPageResponse.builder()
                        .users(userService.listAllUsersByPage(request))
                        .totalUsers(userService.usersCount(request))
                        .build();
        return new ResponseEntity<>(listUsersByPageResponse, HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping("/list-by-house")
    public ResponseEntity<List<UserShortDTO>> listUsersByHouseCode(
            @RequestBody ListUserRequest request) {
        return new ResponseEntity<>(userService.listUsersByHouseCode(request), HttpStatus.OK);
    }

    @PostMapping("/add-role")
    public ResponseEntity<Response> addRole(@Valid @RequestBody AddRoleToUserRequest request) {
        try {
            userService.addRoleToUser(request.getUserId(), request.getRoleId());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove-role")
    public ResponseEntity<Response> removeRole(@Valid @RequestBody RemoveRoleRequest request) {
        try {
            userService.removeRoleFromUser(request.getUserId(), request.getRoleId());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove-house")
    public ResponseEntity<Response> removeHouse(@Valid @RequestBody UserToHouseRequest request) {
        try {
            userService.removeHouseFromUser(request.getUserId(), request.getHouseCode());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            userService.forgotPassword(request.getEmail());
            return new ResponseEntity<>(
                    Response.builder().success(true).message("Forgot password success").build(),
                    HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("forgot password not success")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("reset password success").build(),
                    HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Password cannot be reset").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PutMapping(value = "/update/{id}", produces = "application/json")
    public ResponseEntity<Response> update(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            userService.updateUser(id, request);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("updated the user").build(),
                    HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUsers(@PathVariable long userId) {
        try {
            return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
        } catch (UserServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("User could not be found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
