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
import static org.mockito.Mockito.*;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.ClientDocumentDTO;
import com.onedoorway.project.exception.ClientDocumentServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientDocument;
import com.onedoorway.project.model.Folder;
import com.onedoorway.project.model.NoticeStatus;
import com.onedoorway.project.repository.ClientDocumentRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FolderRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@Log4j2
@ExtendWith(MockitoExtension.class)
class ClientDocumentServiceTest {
    @Mock ClientDocumentRepository mockClientDocumentRepository;

    @Mock ClientRepository mockClientRepository;

    @Mock FolderRepository mockFolderRepository;

    @Mock BlobServiceClientBuilder mockBlobServiceClientBuilder;

    private ClientDocumentService clientDocumentService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        clientDocumentService =
                new ClientDocumentService(
                        mockClientDocumentRepository,
                        mockClientRepository,
                        mockFolderRepository,
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
        long folderId = 2L;
        Client client = Client.builder().id(clientId).name("test").build();
        Folder folder = Folder.builder().id(folderId).folderName("Check").build();
        String lastUploadedBy = "maria";
        InputStream uploadStream = new ByteArrayInputStream("test data".getBytes());
        long fileSize = 100;
        String name = "photo2";
        String docName = "phto";
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
        when(mockClientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(mockFolderRepository.findById(folderId)).thenReturn(Optional.of(folder));

        // When
        clientDocumentService.storeDocument(
                uploadStream,
                docName,
                fileSize,
                client.getId(),
                folder.getId(),
                contentType,
                lastUploadedBy);

        // Then
        ClientDocument expected =
                ClientDocument.builder()
                        .client(client)
                        .folder(folder)
                        .docName(docName)
                        .status(NoticeStatus.ACTIVE)
                        .blobName(name)
                        .lastUploadedBy(lastUploadedBy)
                        .lastUpdatedAt(context.now())
                        .build();
        ArgumentCaptor<ClientDocument> clientDocumentArgumentCaptor =
                ArgumentCaptor.forClass(ClientDocument.class);
        verify(mockClientDocumentRepository).save(clientDocumentArgumentCaptor.capture());
        ClientDocument actual = clientDocumentArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testListDocuments_Success() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        Folder folder = Folder.builder().id(id).client(client).folderName("folder 1").build();
        ClientDocument clientDocument =
                ClientDocument.builder()
                        .client(client)
                        .folder(folder)
                        .docName("file 1")
                        .blobName("blob 1")
                        .status(NoticeStatus.ACTIVE)
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockClientDocumentRepository.findAllByClient_IdAndFolder_Id(1L, 1L))
                .thenReturn(List.of(clientDocument));
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mockFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse("a-mock-connection-string");
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");
            CloudBlockBlob blob = container.getBlockBlobReference(clientDocument.getBlobName());
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

            String url = blob.getUri() + "?" + sas;

            // When
            List<ClientDocumentDTO> clientDocumentDTOS =
                    clientDocumentService.listDocuments(1L, 1L);
            // Then
            assertEquals(1, clientDocumentDTOS.size());
            assertThat(
                    clientDocumentDTOS,
                    (allOf(
                            everyItem(hasProperty("id", equalTo(clientDocument.getId()))),
                            everyItem(
                                    hasProperty(
                                            "clientName",
                                            equalTo(clientDocument.getClient().getName()))),
                            everyItem(
                                    hasProperty(
                                            "folderName",
                                            equalTo(clientDocument.getFolder().getFolderName()))),
                            everyItem(hasProperty("docName", equalTo(clientDocument.getDocName()))),
                            everyItem(
                                    hasProperty("blobName", equalTo(clientDocument.getBlobName()))),
                            everyItem(hasProperty("blobUrl", equalTo(url))),
                            everyItem(
                                    hasProperty(
                                            "status", equalTo(clientDocument.getStatus().name()))),
                            everyItem(
                                    hasProperty(
                                            "lastUploadedBy",
                                            equalTo(clientDocument.getLastUploadedBy()))),
                            everyItem(hasProperty("lastUpdatedAt", equalTo(context.now()))))));
        }
    }

    @SneakyThrows
    @Test
    void testListDocuments_Success_Empty() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        Folder folder = Folder.builder().id(id).client(client).folderName("folder 1").build();
        when(mockFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);

            // When
            List<ClientDocumentDTO> clientDocumentDTOS =
                    clientDocumentService.listDocuments(1L, 1L);

            // Then
            assertEquals(0, clientDocumentDTOS.size());
        }
    }

    @SneakyThrows
    @Test
    void testListDocuments_Failure_FolderNotFound() {
        // Given
        long id = 1;
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);

            Client client = Client.builder().id(id).name("client").build();
            when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));

            assertThrows(
                    ClientDocumentServiceException.class,
                    () -> {
                        // When
                        clientDocumentService.listDocuments(1L, 1L);
                    });
        }
    }

    @SneakyThrows
    @Test
    void testListDocuments_Failure_ClientNotFound() {
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();

        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);

            assertThrows(
                    ClientDocumentServiceException.class,
                    () -> {
                        // When
                        clientDocumentService.listDocuments(1L, 1L);
                    });
        }
    }

    @SneakyThrows
    @Test
    void testDeleteDocumentById_Success() {
        long clientId = 1L;
        long folderId = 2L;
        Client client = Client.builder().id(clientId).name("test").build();
        Folder folder = Folder.builder().id(folderId).folderName("Check").build();
        String name = "photo2";
        String docName = "medical";
        ClientDocument clientDocument =
                ClientDocument.builder()
                        .client(client)
                        .folder(folder)
                        .docName(docName)
                        .status(NoticeStatus.ACTIVE)
                        .blobName(name)
                        .lastUploadedBy("Sam")
                        .lastUpdatedAt(context.now())
                        .build();
        when(mockClientDocumentRepository.findById(1L)).thenReturn(Optional.of(clientDocument));
        // When
        clientDocumentService.deleteDocumentById(1L);
        ClientDocument expected =
                ClientDocument.builder()
                        .client(client)
                        .folder(folder)
                        .docName(docName)
                        .status(NoticeStatus.INACTIVE)
                        .blobName(name)
                        .lastUploadedBy("Sam")
                        .lastUpdatedAt(context.now())
                        .build();
        // Then
        verify(mockClientDocumentRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    void testDeleteDocument_Failure_DocumentNotFound() {
        assertThrows(
                ClientDocumentServiceException.class,
                () -> {
                    // When
                    clientDocumentService.deleteDocumentById(1L);
                });
    }
}
