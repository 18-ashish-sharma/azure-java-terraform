package com.onedoorway.project.controller;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.services.ClientService;
import java.security.InvalidKeyException;
import javax.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/client", produces = "application/json")
public class ClientController {

    private final ClientService clientService;

    public ClientController(@Autowired ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/create")
    public ResponseEntity<Response> createClient(@Valid @RequestBody ClientRequest request) {
        clientService.createClient(request);
        return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Response> updateClient(
            @PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        try {
            clientService.updateClient(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("update-additional/{id}")
    public ResponseEntity<Response> updateClientAdditional(
            @PathVariable Long id, @Valid @RequestBody UpdateClientRequest request) {
        try {
            clientService.updateClientAdditional(id, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(value = "/list")
    public ResponseEntity<ListClientResponse> listClient(@RequestBody ListClientRequest request) {
        ListClientResponse listClientResponse =
                ListClientResponse.builder()
                        .clients(clientService.listAllClients(request))
                        .totalClients(clientService.clientsCount(request))
                        .build();
        return new ResponseEntity<>(listClientResponse, HttpStatus.OK);
    }

    @PostMapping("/create-contact")
    public ResponseEntity<Response> createClientContact(
            @Valid @RequestBody ClientContactRequest request) {
        try {
            clientService.createClientContact(request);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("client contact created").build(),
                    HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("cannot create client contact")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/contact/{id}")
    public ResponseEntity<?> getClientContact(@PathVariable long id) {
        try {
            return new ResponseEntity<>(clientService.getClientContactById(id), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("Client Contact could not be found")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/contact/{id}")
    public ResponseEntity<Response> updateClientContact(
            @PathVariable Long id, @Valid @RequestBody UpdateClientContactRequest request) {
        try {
            clientService.updateClientContact(id, request);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("updated client contact").build(),
                    HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder()
                            .success(false)
                            .message("cannot update client contact")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/list/contact/{clientId}")
    public ResponseEntity<?> listClientContacts(@PathVariable Long clientId) {
        try {
            return new ResponseEntity<>(clientService.listClientContacts(clientId), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("client not found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create-folder")
    public ResponseEntity<Response> createFolder(@Valid @RequestBody FolderRequest request) {
        try {
            clientService.createFolder(request);
            return new ResponseEntity<>(
                    Response.builder().success(true).message("folder created").build(),
                    HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("cannot create folder").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/folder/{id}")
    public ResponseEntity<?> getFolder(@PathVariable long id) {
        try {
            return new ResponseEntity<>(clientService.getFolderById(id), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Folder could not be found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping(value = "/list/folders/{clientId}")
    public ResponseEntity<?> listFolders(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(clientService.listFoldersById(clientId), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Folder could not be found").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-folder")
    public ResponseEntity<Response> updateFolder(@Valid @RequestBody UpdateFolderRequest request) {
        try {
            clientService.updateFolder(request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("cannot update folder").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @GetMapping("/get/{clientId}")
    public ResponseEntity<?> getClientById(@PathVariable long clientId) {
        try {
            return new ResponseEntity<>(clientService.getClientById(clientId), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("CLient could not be found").build(),
                    HttpStatus.BAD_REQUEST);
        } catch (InvalidKeyException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message("Cannot get Url").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PutMapping("/update/medicare/{clientId}")
    public ResponseEntity<Response> updateClientById(
            @PathVariable Long clientId, @Valid @RequestBody UpdateClientRequest request) {
        try {
            clientService.updateClientById(clientId, request);
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().success(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @SneakyThrows
    @PostMapping(
            value = "/upload-photo/{clientId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response> uploadPhoto(
            @RequestPart("clientId") long clientId,
            @RequestPart("name") String name,
            @RequestPart("file") MultipartFile file) {
        try {
            clientService.storePhoto(
                    file.getInputStream(), file.getSize(), clientId, name, file.getContentType());
            return new ResponseEntity<>(Response.builder().success(true).build(), HttpStatus.OK);
        } catch (ClientServiceException e) {
            return new ResponseEntity<>(
                    Response.builder().message("Invalid stream").build(), HttpStatus.BAD_REQUEST);
        }
    }
}
