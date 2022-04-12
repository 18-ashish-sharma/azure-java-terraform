package com.onedoorway.project.controller;

import com.onedoorway.project.dto.LoginRequest;
import com.onedoorway.project.dto.LoginResponse;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.UserServiceException;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.services.UserService;
import com.onedoorway.project.util.JwtUtil;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final ODWUserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(
            AuthenticationManager authenticationManager,
            ODWUserDetailsService userDetailsService,
            UserService userService,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest)
            throws UserServiceException {
        try {
            if (userService.isUserDeleted(loginRequest.getEmail())) {
                return loginFailure();
            }

            if (!userService.isUserDeleted(loginRequest.getEmail())) {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(), loginRequest.getPassword()));
                final ODWUserDetails userDetails =
                        (ODWUserDetails)
                                userDetailsService.loadUserByUsername(loginRequest.getEmail());
                log.info("Successfully authenticated the user {}", loginRequest.getEmail());
                return loginSuccess(loginRequest, userDetails);
            } else {
                throw new BadCredentialsException("User is deleted");
            }
        } catch (BadCredentialsException e) {
            log.error(
                    "Incorrect username or password for username {} or User deleted {}",
                    loginRequest.getEmail(),
                    e.getMessage());
            log.info(e.getMessage());
            return loginFailure();
        }
    }

    private ResponseEntity<?> loginFailure() {

        return new ResponseEntity<>(
                Response.builder()
                        .success(false)
                        .message("Incorrect User Name or Password")
                        .build(),
                HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<LoginResponse> loginSuccess(
            LoginRequest loginRequest, ODWUserDetails userDetails) {
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .jwt(jwtUtil.generateToken(userDetails))
                        .roles(
                                userDetails.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.toList()))
                        .houseCode(
                                userDetails.getHouses().stream()
                                        .map(House::getHouseCode)
                                        .collect(Collectors.toList()))
                        .firstName(userDetails.getFirstName())
                        .lastName(userDetails.getLastName())
                        .build());
    }
}
