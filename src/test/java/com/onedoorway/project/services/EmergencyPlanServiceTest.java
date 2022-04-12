package com.onedoorway.project.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.EmergencyPlanDTO;
import com.onedoorway.project.dto.GetEmergencyPlanRequest;
import com.onedoorway.project.exception.EmergencyPlanServiceException;
import com.onedoorway.project.model.EmergencyPlan;
import com.onedoorway.project.repository.EmergencyPlanRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
class EmergencyPlanServiceTest {
    @Mock EmergencyPlanRepository mockEmergencyPlanRepository;
    @Mock BlobServiceClientBuilder mockBlobServiceClientBuilder;

    private EmergencyPlanService emergencyPlanService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        emergencyPlanService =
                new EmergencyPlanService(
                        mockEmergencyPlanRepository,
                        mockBlobServiceClientBuilder,
                        "a-mock-connection-string",
                        "test-container",
                        context);
    }

    @SneakyThrows
    @Test
    void testStoreFile() {
        // Given
        String houseCode = "100";
        String fileType = "PLAN";
        InputStream uploadStream = new ByteArrayInputStream("test data".getBytes());
        long fileSize = 100;
        String blobName = "emergencyplan100.pdf";
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        when(mockBlobServiceClientBuilder.connectionString(any()))
                .thenReturn(mockBlobServiceClientBuilder);
        when(mockBlobServiceClientBuilder.buildClient()).thenReturn(serviceClient);
        BlobContainerClient containerClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(anyString())).thenReturn(containerClient);
        BlobClient blobClient = mock(BlobClient.class);
        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobName()).thenReturn(blobName);

        // When
        emergencyPlanService.storeFile(uploadStream, fileSize, houseCode, fileType);

        // Then
        EmergencyPlan expected =
                EmergencyPlan.builder().houseCode(houseCode).urlEmergencyPlan(blobName).build();

        ArgumentCaptor<EmergencyPlan> emergencyPlanArgumentCaptor =
                ArgumentCaptor.forClass(EmergencyPlan.class);
        verify(mockEmergencyPlanRepository).save(emergencyPlanArgumentCaptor.capture());
        EmergencyPlan actual = emergencyPlanArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testGetPlanUrl_Success() {
        EmergencyPlan emergencyPlan =
                EmergencyPlan.builder().houseCode("100").urlEmergencyPlan("testplan.pdf").build();
        GetEmergencyPlanRequest request =
                GetEmergencyPlanRequest.builder().houseCode("100").build();

        when(mockEmergencyPlanRepository.findByHouseCode("100"))
                .thenReturn(Optional.of(emergencyPlan));
        // Using the development storage account for unit testing
        CloudBlockBlob blob;
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

            if (emergencyPlan.getUrlEmergencyPlan() != null) {
                blob = container.getBlockBlobReference(emergencyPlan.getUrlEmergencyPlan());
            } else {
                blob = container.getBlockBlobReference(emergencyPlan.getUrlEmergencyHandout());
            }
            String sas =
                    blob.getUri()
                            + "?"
                            + blob.generateSharedAccessSignature(accessBlobPolicy, null);
            EmergencyPlanDTO expected =
                    EmergencyPlanDTO.builder()
                            .houseCode(emergencyPlan.getHouseCode())
                            .emergencyPlanUrl(sas)
                            .emergencyHandoutUrl("")
                            .build();

            // When
            EmergencyPlanDTO actual = emergencyPlanService.getPlanUrl(request);

            // Then
            assertEquals(expected, actual);
        }
    }

    @SneakyThrows
    @Test
    void testGetPlanUrl_Failure() {
        // Given
        String houseCode = "100";
        GetEmergencyPlanRequest request =
                GetEmergencyPlanRequest.builder().houseCode("100").build();

        assertThrows(
                EmergencyPlanServiceException.class,
                () -> {
                    // When
                    emergencyPlanService.getPlanUrl(request);
                });
    }
}
