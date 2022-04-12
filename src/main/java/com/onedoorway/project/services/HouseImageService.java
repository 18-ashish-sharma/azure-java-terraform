package com.onedoorway.project.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.Context;
import com.onedoorway.project.dto.GetHouseImageRequest;
import com.onedoorway.project.exception.HouseImageServiceException;
import com.onedoorway.project.model.HouseImage;
import com.onedoorway.project.repository.HouseImageRepository;
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
public class HouseImageService {
    private final HouseImageRepository houseImageRepository;
    private final BlobServiceClientBuilder blobServiceClientBuilder;
    private final String connectionString;
    private final String containerName;
    private final Context context;

    @Autowired
    public HouseImageService(
            HouseImageRepository houseImageRepository,
            BlobServiceClientBuilder blobServiceClientBuilder,
            @Value("${azure.blob.connection-string}") String connectionString,
            @Value("${azure.blob.container.name}") String containerName,
            Context context) {
        this.houseImageRepository = houseImageRepository;
        this.blobServiceClientBuilder = blobServiceClientBuilder;
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.context = context;
    }

    @PreAuthorize("@context.isAdmin()")
    public void storeImage(
            InputStream data,
            long size,
            String contentType,
            String houseCode,
            String lastUploadedBy)
            throws HouseImageServiceException {
        BlobServiceClient blobServiceClient =
                blobServiceClientBuilder.connectionString(connectionString).buildClient();
        BlobContainerClient blobContainerClient =
                blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient =
                blobContainerClient.getBlobClient(
                        String.format("photo/housephoto_%s_%s.jpg", houseCode, UUID.randomUUID()));
        if (!(contentType.equals("image/jpg")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/png"))) {
            log.error("unsupported format");
            throw new HouseImageServiceException("jpg/png file type are only supported");
        }
        blobClient.upload(data, size);
        String name = blobClient.getBlobName();
        log.info("Uploaded the blob to container - name {}", name);
        Optional<HouseImage> houseImage = houseImageRepository.findByHouseCode(houseCode);
        HouseImage image = houseImage.orElse(HouseImage.builder().houseCode(houseCode).build());
        image.setLastUploadedBy(lastUploadedBy);
        image.setLastUpdatedAt(context.now());
        image.setImageBlobName(name);
        houseImageRepository.save(image);
        log.info(
                "Upserted the image for house {} in container by lastUploadedBy {}",
                houseCode,
                lastUploadedBy);
    }

    public String getImageUrl(GetHouseImageRequest request)
            throws URISyntaxException, InvalidKeyException, StorageException {
        Optional<HouseImage> houseImage =
                houseImageRepository.findByHouseCode(request.getHouseCode());
        if (houseImage.isEmpty()) {
            log.error("houseImage not found");
            throw new InvalidKeyException("HouseImage not found");
        }

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
        CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);

        CloudBlockBlob blob = container.getBlockBlobReference(houseImage.get().getImageBlobName());

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

        log.info("Fetched the urls based on houseCode {} ", request.getHouseCode());
        return sasUrl;
    }
}
