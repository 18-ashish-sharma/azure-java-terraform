package com.onedoorway.project.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.Context;
import com.onedoorway.project.dto.EmergencyPlanDTO;
import com.onedoorway.project.dto.GetEmergencyPlanRequest;
import com.onedoorway.project.exception.EmergencyPlanServiceException;
import com.onedoorway.project.model.EmergencyPlan;
import com.onedoorway.project.repository.EmergencyPlanRepository;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class EmergencyPlanService {
    private final EmergencyPlanRepository emergencyPlanRepository;
    private final BlobServiceClientBuilder blobServiceClientBuilder;
    private final String connectionString;
    private final String containerName;
    private final Context context;

    @Autowired
    public EmergencyPlanService(
            EmergencyPlanRepository emergencyPlanRepository,
            BlobServiceClientBuilder blobServiceClientBuilder,
            @Value("${azure.blob.connection-string}") String connectionString,
            @Value("${azure.blob.container.name}") String containerName,
            Context context) {
        this.emergencyPlanRepository = emergencyPlanRepository;
        this.blobServiceClientBuilder = blobServiceClientBuilder;
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.context = context;
    }

    @PreAuthorize("@context.isAdmin()")
    public void storeFile(InputStream data, long size, String houseCode, String fileType)
            throws EmergencyPlanServiceException {

        String modifiedHouseCode = houseCode;
        modifiedHouseCode = modifiedHouseCode.replaceAll("[^a-zA-Z0-9 ]", "-");
        BlobServiceClient blobServiceClient =
                blobServiceClientBuilder.connectionString(connectionString).buildClient();
        BlobContainerClient blobContainerClient =
                blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient =
                blobContainerClient.getBlobClient(
                        String.format(
                                "emergency_%s_%s_%s.pdf",
                                fileType, modifiedHouseCode, UUID.randomUUID()));
        blobClient.upload(data, size);

        String name = blobClient.getBlobName();
        log.info("Uploaded the blob to container - name {}", name);

        Optional<EmergencyPlan> emergencyPlan = emergencyPlanRepository.findByHouseCode(houseCode);
        EmergencyPlan entity =
                emergencyPlan.orElse(EmergencyPlan.builder().houseCode(houseCode).build());
        if ("PLAN".equals(fileType)) {
            entity.setUrlEmergencyPlan(name);
        } else {
            entity.setUrlEmergencyHandout(name);
        }
        emergencyPlanRepository.save(entity);
        log.info("Upserted the data for {} in container for house {}", fileType, houseCode);
    }

    public EmergencyPlanDTO getPlanUrl(GetEmergencyPlanRequest request)
            throws EmergencyPlanServiceException, URISyntaxException, InvalidKeyException,
                    StorageException {
        CloudBlockBlob blob;
        EmergencyPlanDTO emergencyPlanDTO = new EmergencyPlanDTO();
        Optional<EmergencyPlan> res =
                emergencyPlanRepository.findByHouseCode(request.getHouseCode());
        if (res.isEmpty()) {
            throw new EmergencyPlanServiceException(
                    "No url with houseCode " + request.getHouseCode());
        }

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
        CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);

        if (res.get().getUrlEmergencyPlan() == null) {
            emergencyPlanDTO.setEmergencyPlanUrl("");
        } else {
            blob = container.getBlockBlobReference(res.get().getUrlEmergencyPlan());
            emergencyPlanDTO.setEmergencyPlanUrl(getUrl(blob));
        }

        if (res.get().getUrlEmergencyHandout() == null) {
            emergencyPlanDTO.setEmergencyHandoutUrl("");
        } else {
            blob = container.getBlockBlobReference(res.get().getUrlEmergencyHandout());
            emergencyPlanDTO.setEmergencyHandoutUrl(getUrl(blob));
        }

        emergencyPlanDTO.setHouseCode(res.get().getHouseCode());
        log.info("Fetched the urls based on houseCode {} ", request.getHouseCode());
        return emergencyPlanDTO;
    }

    public String getUrl(CloudBlockBlob blob) throws InvalidKeyException, StorageException {
        SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

        calendar.add(Calendar.MINUTE, 10);
        accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
        accessBlobPolicy.setPermissions(
                EnumSet.of(
                        SharedAccessBlobPermissions.READ,
                        SharedAccessBlobPermissions.WRITE,
                        SharedAccessBlobPermissions.LIST));
        String sas = blob.generateSharedAccessSignature(accessBlobPolicy, null);

        String sasUrl = blob.getUri() + "?" + sas;
        log.info("SAS url {}", sasUrl);
        return sasUrl;
    }
}
