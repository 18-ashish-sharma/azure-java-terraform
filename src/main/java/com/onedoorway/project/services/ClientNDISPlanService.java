package com.onedoorway.project.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.Context;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.ClientNDISPlanServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientNDISPlanRepository;
import com.onedoorway.project.repository.ClientRepository;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Log4j2
public class ClientNDISPlanService {
    private final ClientRepository clientRepository;
    private final ClientNDISPlanRepository clientNDISPlanRepository;
    private final BlobServiceClientBuilder blobServiceClientBuilder;
    private final String connectionString;
    private final String containerName;
    private final Context context;

    @Autowired
    public ClientNDISPlanService(
            ClientRepository clientRepository,
            ClientNDISPlanRepository clientNDISPlanRepository,
            BlobServiceClientBuilder blobServiceClientBuilder,
            @Value("${azure.blob.connection-string}") String connectionString,
            @Value("${azure.blob.container.name}") String containerName,
            Context context) {
        this.clientRepository = clientRepository;
        this.clientNDISPlanRepository = clientNDISPlanRepository;
        this.blobServiceClientBuilder = blobServiceClientBuilder;
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.context = context;
    }

    public void createClientNDISPlan(ClientNDISPlanRequest request)
            throws ClientNDISPlanServiceException {
        log.info("Creating Client NDIS plan for client id {}", request.getClientId());

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new ClientNDISPlanServiceException("Client not found with client id");
        }
        if (clientNDISPlanRepository.findByClient_IdAndStartDateAndEndDateAndFundingTypeAndLevel(
                        request.getClientId(),
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getFundingType(),
                        request.getLevel())
                != null) {
            log.error(
                    "Client NDIS plan already exists for client id {}, startDate {}, endDate {}, fundingType {}, and level {}",
                    request.getClientId(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getFundingType(),
                    request.getLevel());
            throw new ClientNDISPlanServiceException(
                    "Client NDIS Plan already exists for client id for the given start date, end date, funding type and level");
        }

        ClientNDISPlan clientNDISPlan =
                ClientNDISPlan.builder()
                        .client(client.get())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .fundingType(request.getFundingType())
                        .level(request.getLevel())
                        .deleted(false)
                        .supportDocument("")
                        .otherDocument("")
                        .lastUploadedBy(request.getLastUploadedBy())
                        .build();
        clientNDISPlanRepository.save(clientNDISPlan);

        log.info("Created Client NDIS Plan with Id {}", clientNDISPlan.getId());
    }

    @SneakyThrows
    public List<ClientNDISPlanDTO> getClientNDISPlan(long clientId)
            throws ClientNDISPlanServiceException {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
        CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        CloudBlockBlob blobSupportDocument;
        CloudBlockBlob blobOtherDocument;
        SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MINUTE, 10);
        accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
        accessBlobPolicy.setPermissions(
                EnumSet.of(
                        SharedAccessBlobPermissions.READ,
                        SharedAccessBlobPermissions.WRITE,
                        SharedAccessBlobPermissions.LIST));
        String sasSupportDocument;
        String sasSupportDocumentUrl;
        String sasOtherDocument;
        String sasOtherDocumentUrl;

        List<ClientNDISPlanDTO> clientNDISPlanDTOS = new ArrayList<>();
        List<ClientNDISPlan> clientNDISPlan = clientNDISPlanRepository.findByClientId(clientId);
        if (clientNDISPlan.isEmpty()) {
            throw new ClientNDISPlanServiceException(
                    "no client ndis plan found with clientId{} " + clientId);
        }
        for (int i = 0; i < clientNDISPlan.size(); i++) {
            ClientNDISPlanDTO clientNDISPlanDTO = new ClientNDISPlanDTO();
            clientNDISPlanDTO.setId(clientNDISPlan.get(i).getId());
            clientNDISPlanDTO.setClientId(clientNDISPlan.get(i).getClient().getId());
            clientNDISPlanDTO.setStartDate(String.valueOf(clientNDISPlan.get(i).getStartDate()));
            clientNDISPlanDTO.setEndDate(String.valueOf(clientNDISPlan.get(i).getEndDate()));
            clientNDISPlanDTO.setFundingType(clientNDISPlan.get(i).getFundingType());
            clientNDISPlanDTO.setLevel(clientNDISPlan.get(i).getLevel());
            clientNDISPlanDTO.setDeleted(clientNDISPlan.get(i).getDeleted());
            if (clientNDISPlan.get(i).getSupportDocument().isEmpty()) {
                clientNDISPlanDTO.setSupportDocument("");
            } else {
                blobSupportDocument =
                        container.getBlockBlobReference(clientNDISPlan.get(i).getSupportDocument());
                sasSupportDocument =
                        blobSupportDocument.generateSharedAccessSignature(accessBlobPolicy, null);
                sasSupportDocumentUrl = blobSupportDocument.getUri() + "?" + sasSupportDocument;
                clientNDISPlanDTO.setSupportDocument(sasSupportDocumentUrl);
            }

            if (clientNDISPlan.get(i).getOtherDocument().isEmpty()) {
                clientNDISPlanDTO.setOtherDocument("");
            } else {
                blobOtherDocument =
                        container.getBlockBlobReference(clientNDISPlan.get(i).getOtherDocument());
                sasOtherDocument =
                        blobOtherDocument.generateSharedAccessSignature(accessBlobPolicy, null);
                sasOtherDocumentUrl = blobOtherDocument.getUri() + "?" + sasOtherDocument;
                clientNDISPlanDTO.setOtherDocument(sasOtherDocumentUrl);
            }
            clientNDISPlanDTO.setLastUploadedBy(clientNDISPlan.get(i).getLastUploadedBy());
            clientNDISPlanDTOS.add(clientNDISPlanDTO);
        }
        log.info("clientNDISPlanDTOS {}", clientNDISPlanDTOS);
        if (clientNDISPlanDTOS == null) {
            String errorMessage =
                    String.format("Cannot get clientNDISPlan for client {}", clientId);
            log.error(errorMessage);
            throw new ClientNDISPlanServiceException("clientNDISPlan not found");
        }
        log.info("Fetched all clientNDISPlan - count {}", clientNDISPlanDTOS.size());
        return clientNDISPlanDTOS;
    }

    public void updateClientNDISPlan(Long id, UpdateClientNDISPlanRequest request)
            throws ClientNDISPlanServiceException {
        Optional<ClientNDISPlan> clientNDISPlan = clientNDISPlanRepository.findById(id);
        if (clientNDISPlan.isPresent()) {
            ClientNDISPlan existingEntity = clientNDISPlan.get();
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.map(request, existingEntity);
            existingEntity.setStartDate(LocalDate.parse(request.getStartDate()));
            existingEntity.setEndDate(LocalDate.parse(request.getEndDate()));
            log.info("Updated the client for the given id {}", id);
            clientNDISPlanRepository.save(existingEntity);
        } else {
            throw new ClientNDISPlanServiceException("Client ndis plan not found");
        }
    }

    @PreAuthorize("@context.isAdmin()")
    public void storeDocument(
            InputStream data,
            long size,
            long clientNDISPlanId,
            String clientName,
            String contentType)
            throws ClientNDISPlanServiceException {
        BlobServiceClient blobServiceClient =
                blobServiceClientBuilder.connectionString(connectionString).buildClient();
        BlobContainerClient blobContainerClient =
                blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient;
        Optional<ClientNDISPlan> clientNDISPlan =
                clientNDISPlanRepository.findById(clientNDISPlanId);
        if (contentType.equals("image/jpg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/support/document_%s_%s.jpg",
                                    clientName, UUID.randomUUID()));
        } else if (contentType.equals("image/jpeg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/support/document_%s_%s.jpeg",
                                    clientName, UUID.randomUUID()));
        } else if (contentType.equals("image/png")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/support/document_%s_%s.png",
                                    clientName, UUID.randomUUID()));
        } else if (contentType.equals("application/pdf")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/support/document_%s_%s.pdf",
                                    clientName, UUID.randomUUID()));
        }
        //        else if (contentType.equals("application/vnd.ms-excel")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/support/document_%s_%s.xls",
        //                                    clientName, UUID.randomUUID()));
        //        } else if (contentType.equals(
        //                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/support/document_%s_%s.xlsx",
        //                                    clientName, UUID.randomUUID()));
        //        } else if (contentType.equals("application/msword")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/support/document_%s_%s.doc",
        //                                    clientName, UUID.randomUUID()));
        //        } else if (contentType.equals(
        //
        // "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/support/document_%s_%s.docx",
        //                                    clientName, UUID.randomUUID()));
        //        }
        else {
            log.error("unsupported format");
            throw new ClientNDISPlanServiceException(
                    //                    "jpg/jpeg/png/pdf/docx/xlsx file types are only supported"
                    "jpg/jpeg/png/pdf file types are only supported");
        }
        blobClient.upload(data, size);
        String name = blobClient.getBlobName();
        if (clientNDISPlan.isPresent()) {
            ClientNDISPlan existingEntity = clientNDISPlan.get();
            existingEntity.setSupportDocument(name);
        }
        log.info("Uploaded the blob to container - name {}", name);
        ClientNDISPlan document =
                clientNDISPlan.orElse(ClientNDISPlan.builder().supportDocument(clientName).build());
        clientNDISPlanRepository.save(document);
        log.info("Upserted the document for clientName {} in container", clientName);
    }

    @PreAuthorize("@context.isAdmin()")
    public void storeOtherDocument(
            InputStream data,
            long size,
            long clientNDISPlanId,
            String clientName,
            String contentType)
            throws ClientNDISPlanServiceException {
        BlobServiceClient blobServiceClient =
                blobServiceClientBuilder.connectionString(connectionString).buildClient();
        BlobContainerClient blobContainerClient =
                blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient;
        Optional<ClientNDISPlan> clientNDISPlan =
                clientNDISPlanRepository.findById(clientNDISPlanId);
        if (contentType.equals("image/jpg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/other/document_%s_%s.jpg",
                                    clientName, UUID.randomUUID()));
        } else if (contentType.equals("image/jpeg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/other/document_%s_%s.jpeg",
                                    clientName, UUID.randomUUID()));
        } else if (contentType.equals("image/png")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/other/document_%s_%s.png",
                                    clientName, UUID.randomUUID()));
        } else if (contentType.equals("application/pdf")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format(
                                    "ndis/other/document_%s_%s.pdf",
                                    clientName, UUID.randomUUID()));
        }
        //        else if (contentType.equals("application/vnd.ms-excel")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/other/document_%s_%s.xls",
        //                                    clientName, UUID.randomUUID()));
        //        } else if (contentType.equals(
        //                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/other/document_%s_%s.xlsx",
        //                                    clientName, UUID.randomUUID()));
        //        } else if (contentType.equals("application/msword")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/other/document_%s_%s.doc",
        //                                    clientName, UUID.randomUUID()));
        //        } else if (contentType.equals(
        //
        // "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
        //            blobClient =
        //                    blobContainerClient.getBlobClient(
        //                            String.format(
        //                                    "ndis/other/document_%s_%s.docx",
        //                                    clientName, UUID.randomUUID()));
        //        }
        else {
            log.error("unsupported format");
            throw new ClientNDISPlanServiceException(
                    //                    "jpg/jpeg/png/pdf/docx/xlsx file types are only supported"
                    "jpg/jpeg/png/pdf file types are only supported");
        }
        blobClient.upload(data, size);
        String name = blobClient.getBlobName();
        if (clientNDISPlan.isPresent()) {
            ClientNDISPlan existingEntity = clientNDISPlan.get();
            existingEntity.setOtherDocument(name);
        }
        log.info("Uploaded the blob to container - name {}", name);
        ClientNDISPlan document =
                clientNDISPlan.orElse(ClientNDISPlan.builder().otherDocument(clientName).build());
        clientNDISPlanRepository.save(document);
        log.info("Upserted the document for clientName {} in container", clientName);
    }
}
