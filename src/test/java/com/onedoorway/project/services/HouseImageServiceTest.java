package com.onedoorway.project.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.GetHouseImageRequest;
import com.onedoorway.project.model.HouseImage;
import com.onedoorway.project.repository.HouseImageRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class HouseImageServiceTest {
    @Mock HouseImageRepository mockHouseImageRepository;
    @Mock BlobServiceClientBuilder mockBlobServiceClientBuilder;

    private HouseImageService houseImageService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        houseImageService =
                new HouseImageService(
                        mockHouseImageRepository,
                        mockBlobServiceClientBuilder,
                        "a-mock-connection-string",
                        "test-container",
                        context);
    }

    @SneakyThrows
    @Test
    void testStoreImage() {
        // Given
        String houseCode = "100";
        String lastUploadedBy = "tia";
        InputStream uploadStream = new ByteArrayInputStream("test data".getBytes());
        long fileSize = 100;
        String name = "photo1";
        String contentType = "image/jpg";
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        when(mockBlobServiceClientBuilder.connectionString(any()))
                .thenReturn(mockBlobServiceClientBuilder);
        when(mockBlobServiceClientBuilder.buildClient()).thenReturn(serviceClient);
        BlobContainerClient containerClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(anyString())).thenReturn(containerClient);
        BlobClient blobClient = mock(BlobClient.class);
        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobName()).thenReturn(name);

        // When
        houseImageService.storeImage(
                uploadStream, fileSize, contentType, houseCode, lastUploadedBy);

        // Then
        HouseImage expected =
                HouseImage.builder()
                        .houseCode(houseCode)
                        .imageBlobName(name)
                        .lastUploadedBy(lastUploadedBy)
                        .lastUpdatedAt(context.now())
                        .build();

        ArgumentCaptor<HouseImage> houseImageArgumentCaptor =
                ArgumentCaptor.forClass(HouseImage.class);
        verify(mockHouseImageRepository).save(houseImageArgumentCaptor.capture());
        HouseImage actual = houseImageArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testGetImageUrl_Success() {
        // Given
        HouseImage houseImage =
                HouseImage.builder()
                        .houseCode("100")
                        .imageBlobName("blob")
                        .lastUpdatedAt(context.now())
                        .build();
        GetHouseImageRequest request = GetHouseImageRequest.builder().houseCode("100").build();

        when(mockHouseImageRepository.findByHouseCode("100")).thenReturn(Optional.of(houseImage));
        // Using the development storage account for unit testing
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse("a-mock-connection-string");
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");
            SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            calendar.add(Calendar.MINUTE, 10);
            accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
            accessBlobPolicy.setPermissions(
                    EnumSet.of(
                            SharedAccessBlobPermissions.READ,
                            SharedAccessBlobPermissions.WRITE,
                            SharedAccessBlobPermissions.LIST));
            CloudBlockBlob blob = container.getBlockBlobReference(houseImage.getImageBlobName());
            String expected =
                    blob.getUri()
                            + "?"
                            + blob.generateSharedAccessSignature(accessBlobPolicy, null);
            // When
            String actual = houseImageService.getImageUrl(request);

            // Then
            assertEquals(expected, actual);
        }
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testGetImageUrl_Failure_HouseImageNotFound() {
        // Given
        String houseCode = "109";
        GetHouseImageRequest request = GetHouseImageRequest.builder().houseCode("100").build();

        assertThrows(
                InvalidKeyException.class,
                () -> {
                    // When
                    houseImageService.getImageUrl(request);
                });
    }
}
