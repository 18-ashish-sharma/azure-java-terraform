package com.onedoorway.project.controller;

import com.onedoorway.project.dto.ClientReportDTO;
import com.onedoorway.project.dto.Response;
import com.onedoorway.project.dto.ToggleReportRequest;
import com.onedoorway.project.exception.ClientReportServiceException;
import com.onedoorway.project.services.ClientReportService;
import java.util.List;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/clientReport", produces = "application/json")
public class ClientReportController {

    private final ClientReportService clientReportService;

    public ClientReportController(@Autowired ClientReportService clientReportService) {
        this.clientReportService = clientReportService;
    }

    @SneakyThrows
    @GetMapping("/get/{clientId}")
    public ResponseEntity<List<ClientReportDTO>> getClientReportById(@PathVariable long clientId) {
        return new ResponseEntity<>(
                clientReportService.getClientReportById(clientId), HttpStatus.OK);
    }

    @PostMapping("/toggle-report")
    public ResponseEntity<Response> toggleReport(@Valid @RequestBody ToggleReportRequest request) {
        try {
            clientReportService.toggleReport(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientReportServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Report not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
