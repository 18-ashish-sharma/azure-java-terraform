package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.ClientNDISPlanServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientNDISPlanRepository;
import com.onedoorway.project.repository.ClientRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@Log4j2
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ClientNDISPlanServiceTest {
    @Mock ClientNDISPlanRepository mockClientNDISPlanRepository;
    @Mock ClientRepository mockClientRepository;
    private ClientNDISPlanService clientNDISPlanService;
    @Mock BlobServiceClientBuilder mockBlobServiceClientBuilder;
    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        clientNDISPlanService =
                new ClientNDISPlanService(
                        mockClientRepository,
                        mockClientNDISPlanRepository,
                        mockBlobServiceClientBuilder,
                        "a-mock-connection-string",
                        "test-container",
                        context);
    }

    @SneakyThrows
    @Test
    void testStoreDocument() {
        // Given
        long clientId = 1L;
        Client client = Client.builder().id(clientId).name("client").build();
        InputStream uploadStream = new ByteArrayInputStream("test data".getBytes());
        long fileSize = 100;
        String name = "doc1";
        long clientNDISPlanId = 1;
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
        when(mockClientNDISPlanRepository.findById(clientNDISPlanId))
                .thenReturn(Optional.of(ClientNDISPlan.builder().build()));

        // When
        clientNDISPlanService.storeDocument(
                uploadStream, fileSize, clientNDISPlanId, client.getName(), contentType);

        // Then
        ClientNDISPlan expected = ClientNDISPlan.builder().supportDocument("doc1").build();
        ArgumentCaptor<ClientNDISPlan> clientNDISPlanArgumentCaptor =
                ArgumentCaptor.forClass(ClientNDISPlan.class);
        verify(mockClientNDISPlanRepository).save(clientNDISPlanArgumentCaptor.capture());
        ClientNDISPlan actual = clientNDISPlanArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testStoreOtherDocument() {
        // Given
        long clientId = 1L;
        Client client = Client.builder().id(clientId).name("client").build();
        InputStream uploadStream = new ByteArrayInputStream("test data".getBytes());
        long fileSize = 100;
        String name = "doc1";
        long clientNDISPlanId = 1;
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
        when(mockClientNDISPlanRepository.findById(clientNDISPlanId))
                .thenReturn(Optional.of(ClientNDISPlan.builder().build()));

        // When
        clientNDISPlanService.storeOtherDocument(
                uploadStream, fileSize, clientNDISPlanId, client.getName(), contentType);

        // Then
        ClientNDISPlan expected = ClientNDISPlan.builder().otherDocument("doc1").build();
        ArgumentCaptor<ClientNDISPlan> clientNDISPlanArgumentCaptor =
                ArgumentCaptor.forClass(ClientNDISPlan.class);
        verify(mockClientNDISPlanRepository).save(clientNDISPlanArgumentCaptor.capture());
        ClientNDISPlan actual = clientNDISPlanArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testCreateClientNDISPlan_Success() {
        // Given
        LocalDate startDate = LocalDate.of(2021, 10, 11);
        LocalDate endDate = LocalDate.of(2022, 11, 10);
        String fundingType = "funding2";
        String level = "level";
        String supportDocument = "supportDocs";
        String otherDocument = "otherDocs";
        String lastUploadedBy = "lastUploadedBy";

        ClientNDISPlanRequest request =
                ClientNDISPlanRequest.builder()
                        .clientId(1L)
                        .startDate(startDate)
                        .endDate(endDate)
                        .fundingType(fundingType)
                        .level(level)
                        .supportDocument(supportDocument)
                        .otherDocument(otherDocument)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        clientNDISPlanService.createClientNDISPlan(request);

        // Then
        ClientNDISPlan expected =
                ClientNDISPlan.builder()
                        .client(client)
                        .startDate(startDate)
                        .endDate(endDate)
                        .fundingType(fundingType)
                        .level(level)
                        .deleted(false)
                        .supportDocument(supportDocument)
                        .otherDocument(otherDocument)
                        .lastUploadedBy(lastUploadedBy)
                        .build();
        ArgumentCaptor<ClientNDISPlan> clientNDISPlanArgumentCaptor =
                ArgumentCaptor.forClass(ClientNDISPlan.class);
        verify(mockClientNDISPlanRepository).save(clientNDISPlanArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("startDate", equalTo(startDate)),
                        HasPropertyWithValue.hasProperty("endDate", equalTo(endDate)),
                        HasPropertyWithValue.hasProperty("fundingType", equalTo(fundingType)),
                        HasPropertyWithValue.hasProperty("level", equalTo(level)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty(
                                "supportDocument", equalTo(supportDocument)),
                        HasPropertyWithValue.hasProperty("otherDocument", equalTo(otherDocument)),
                        HasPropertyWithValue.hasProperty(
                                "lastUploadedBy", equalTo(lastUploadedBy)))));
    }

    @SneakyThrows
    @Test
    void testCreateClientNDISPlan_withoutFields() {
        // Given
        LocalDate startDate = LocalDate.of(2021, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 10, 11);
        String fundingType = "fund1";
        String level = "leve1";

        Client client = Client.builder().id(1L).build();
        mockClientRepository.save(client);

        ClientNDISPlanRequest request =
                ClientNDISPlanRequest.builder()
                        .clientId(client.getId())
                        .startDate(startDate)
                        .endDate(endDate)
                        .fundingType(fundingType)
                        .level(level)
                        .build();

        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        clientNDISPlanService.createClientNDISPlan(request);
        // Then
        ClientNDISPlan expected =
                ClientNDISPlan.builder()
                        .client(client)
                        .startDate(startDate)
                        .endDate(endDate)
                        .fundingType(fundingType)
                        .level(level)
                        .build();

        ArgumentCaptor<ClientNDISPlan> clientNDISPlanArgumentCaptor =
                ArgumentCaptor.forClass(ClientNDISPlan.class);
        verify(mockClientNDISPlanRepository).save(clientNDISPlanArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("startDate", equalTo(startDate)),
                        HasPropertyWithValue.hasProperty("endDate", equalTo(endDate)),
                        HasPropertyWithValue.hasProperty("fundingType", equalTo(fundingType)),
                        HasPropertyWithValue.hasProperty("level", equalTo(level)))));
    }

    @Test
    void testCreateClientNDISPlan_Failure_ClientNotFound() {
        // Given
        LocalDate startDate = LocalDate.of(2021, 11, 10);
        LocalDate endDate = LocalDate.of(2022, 11, 10);
        String fundingType = "funding";
        String level = "level";
        String supportDocument = "supportDocs";
        String otherDocument = "otherDocs";
        String lastUploadedBy = "lastUploadedBy";

        ClientNDISPlanRequest request =
                ClientNDISPlanRequest.builder()
                        .clientId(1L)
                        .startDate(startDate)
                        .endDate(endDate)
                        .fundingType(fundingType)
                        .level(level)
                        .supportDocument(supportDocument)
                        .otherDocument(otherDocument)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                ClientNDISPlanServiceException.class,
                () -> {
                    // When
                    clientNDISPlanService.createClientNDISPlan(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetClientNDISPlanById_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();

        ClientNDISPlan clientNDISPlan =
                ClientNDISPlan.builder()
                        .client(client)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now())
                        .fundingType("fundingType")
                        .level("level")
                        .supportDocument("supportDocument")
                        .otherDocument("otherDocument")
                        .lastUploadedBy("lastUploadedBy")
                        .deleted(false)
                        .build();

        when(mockClientNDISPlanRepository.findByClientId(1L)).thenReturn(List.of(clientNDISPlan));
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse("a-mock-connection-string");
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");
            CloudBlockBlob blobSupportDocument =
                    container.getBlockBlobReference(clientNDISPlan.getSupportDocument());
            CloudBlockBlob blobOtherDocument =
                    container.getBlockBlobReference(clientNDISPlan.getOtherDocument());
            SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            calendar.add(Calendar.MINUTE, 10);
            accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
            accessBlobPolicy.setPermissions(
                    EnumSet.of(
                            SharedAccessBlobPermissions.READ,
                            SharedAccessBlobPermissions.WRITE,
                            SharedAccessBlobPermissions.LIST));
            String sasSupportDocument =
                    blobSupportDocument.generateSharedAccessSignature(accessBlobPolicy, null);
            String sasOtherDocument =
                    blobOtherDocument.generateSharedAccessSignature(accessBlobPolicy, null);

            String sasSupportDocumentUrl = blobSupportDocument.getUri() + "?" + sasSupportDocument;
            String sasOtherDocumentUrl = blobOtherDocument.getUri() + "?" + sasOtherDocument;

            List<ClientNDISPlanDTO> clientNDISPlanDTOS =
                    clientNDISPlanService.getClientNDISPlan(1L);

            assertEquals(1, clientNDISPlanDTOS.size());
            assertThat(
                    clientNDISPlanDTOS,
                    (allOf(
                            everyItem(hasProperty("id", equalTo(clientNDISPlan.getId()))),
                            everyItem(hasProperty("clientId", equalTo(client.getId()))),
                            everyItem(
                                    hasProperty(
                                            "startDate",
                                            equalTo(
                                                    clientNDISPlan
                                                            .getStartDate()
                                                            .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                            "yyyy-MM-dd"))))),
                            everyItem(
                                    hasProperty(
                                            "endDate",
                                            equalTo(
                                                    clientNDISPlan
                                                            .getEndDate()
                                                            .format(
                                                                    DateTimeFormatter.ofPattern(
                                                                            "yyyy-MM-dd"))))),
                            everyItem(
                                    hasProperty(
                                            "fundingType",
                                            equalTo(clientNDISPlan.getFundingType()))),
                            everyItem(hasProperty("level", equalTo(clientNDISPlan.getLevel()))),
                            everyItem(hasProperty("deleted", equalTo(clientNDISPlan.getDeleted()))),
                            everyItem(
                                    hasProperty(
                                            "lastUploadedBy",
                                            equalTo(clientNDISPlan.getLastUploadedBy()))),
                            everyItem(
                                    hasProperty("supportDocument", equalTo(sasSupportDocumentUrl))),
                            everyItem(
                                    hasProperty("otherDocument", equalTo(sasOtherDocumentUrl))))));
        }
    }

    @Test
    void testGetClientNDISPlan_Failure_ContactNotFound() {
        // Given
        long id = 1;
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);
            assertThrows(
                    ClientNDISPlanServiceException.class,
                    () -> {
                        // When
                        clientNDISPlanService.getClientNDISPlan(id);
                    });
        }
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientNDIS_Success() {
        // Given
        long id = 1;
        String startDate = "2021-04-06";
        String endDate = "2022-12-03";
        String fundType = "fundType";
        String level = "level";
        Boolean deleted = false;
        String lastUploadedBy = "Sam";

        Client client = Client.builder().id(id).name("client").build();
        mockClientRepository.save(client);

        ClientNDISPlan clientNDISPlan =
                ClientNDISPlan.builder()
                        .client(client)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now())
                        .fundingType("fundingType")
                        .level("level")
                        .supportDocument("supportDocument")
                        .otherDocument("otherDocument")
                        .lastUploadedBy("lastUploadedBy")
                        .deleted(false)
                        .build();

        UpdateClientNDISPlanRequest request =
                UpdateClientNDISPlanRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .deleted(deleted)
                        .fundingType(fundType)
                        .level(level)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        when(mockClientNDISPlanRepository.findById(1L)).thenReturn(Optional.of(clientNDISPlan));

        // When
        clientNDISPlanService.updateClientNDISPlan(id, request);

        ClientNDISPlan expected =
                ClientNDISPlan.builder()
                        .client(client)
                        .startDate(LocalDate.parse(startDate))
                        .endDate(LocalDate.parse(endDate))
                        .deleted(deleted)
                        .fundingType(fundType)
                        .level(level)
                        .lastUploadedBy(lastUploadedBy)
                        .supportDocument("supportDocument")
                        .otherDocument("otherDocument")
                        .build();

        // Then
        verify(mockClientNDISPlanRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientNDISPlan_Failure_ClientNotFound() {
        // Given
        long id = 1;
        String startDate = "2021-04-06";
        String endDate = "2022-12-03";
        String fundType = "fundType";
        String level = "level";
        Boolean deleted = false;
        String lastUploadedBy = "Sam";
        UpdateClientNDISPlanRequest request =
                UpdateClientNDISPlanRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .deleted(deleted)
                        .fundingType(fundType)
                        .level(level)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                ClientNDISPlanServiceException.class,
                () -> {
                    // When
                    clientNDISPlanService.updateClientNDISPlan(id, request);
                });
    }
}
