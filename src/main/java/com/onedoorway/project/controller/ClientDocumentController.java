package com.onedoorway.project.controller;

import com.onedoorway.project.dto.Response;
import com.onedoorway.project.exception.ClientDocumentServiceException;
import com.onedoorway.project.services.ClientDocumentService;
import java.io.IOException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Log4j2
public class ClientDocumentController {

    private final ClientDocumentService clientDocumentService;

    public ClientDocumentController(ClientDocumentService clientDocumentService) {
        this.clientDocumentService = clientDocumentService;
    }

    @PostMapping(
            value = "/upload-document",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadDocument(
            @RequestPart("clientId") long clientId,
            @RequestPart("folderId") long folderId,
            @RequestPart("lastUploadedBy") String lastUploadedBy,
            @RequestPart("docName") String docName,
            @RequestPart("file") MultipartFile file) {
        log.info(
                "uploading document for client {} in folder {} of size {} and type {} and docName {}",
                clientId,
                folderId,
                file.getSize(),
                file.getContentType(),
                docName);
        try {
            clientDocumentService.storeDocument(
                    file.getInputStream(),
                    docName,
                    file.getSize(),
                    clientId,
                    folderId,
                    file.getContentType(),
                    lastUploadedBy);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (IOException e) {
            log.error("Invalid stream for document upload");
            return new ResponseEntity<>(
                    Response.builder().message("Invalid stream").build(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list-documents/{clientId}/{folderId}", produces = "application/json")
    public ResponseEntity<?> listDocuments(
            @PathVariable long clientId, @PathVariable long folderId) {
        try {
            return new ResponseEntity<>(
                    clientDocumentService.listDocuments(clientId, folderId), HttpStatus.OK);
        } catch (ClientDocumentServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("document not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PutMapping(value = "/delete-document/{id}", produces = "application/json")
    public ResponseEntity<Response> deleteDocument(@PathVariable Long id) {
        try {
            clientDocumentService.deleteDocumentById(id);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("deleted document").build(),
                    HttpStatus.OK);
        } catch (ClientDocumentServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("cannot delete document").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
