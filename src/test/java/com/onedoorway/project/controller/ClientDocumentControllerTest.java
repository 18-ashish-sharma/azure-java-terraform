package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientDocumentRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FolderRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ClientDocumentControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private FolderRepository folderRepository;

    @Autowired private ClientDocumentRepository clientDocumentRepository;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().id(1).name("USER").build())))
                        .build();
        userRepository.save(user);

        ODWUserDetails basicUser = new ODWUserDetails(user);
        when(mockJwtUtil.extractUsername(anyString())).thenReturn("test@test.com");
        when(mockJwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(mockUserDetailsService.loadUserByUsername(anyString())).thenReturn(basicUser);
        this.mockMvc =
                webAppContextSetup(this.wac)
                        .addFilters(new JwtRequestFilter(mockUserDetailsService, mockJwtUtil))
                        .build();
    }

    @AfterEach
    public void tearDown() {
        clientDocumentRepository.deleteAll();
        folderRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ list documents")
    void testListDocuments_Success() {
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        Folder folder = Folder.builder().id(id).client(client).folderName("folder 1").build();
        folder = folderRepository.save(folder);
        ClientDocument clientDocument =
                ClientDocument.builder()
                        .client(client)
                        .folder(folder)
                        .docName("file 1")
                        .blobName("blob 1")
                        .status(NoticeStatus.ACTIVE)
                        .lastUploadedBy("sam")
                        .lastUpdatedAt(Instant.now())
                        .build();
        clientDocument = clientDocumentRepository.save(clientDocument);
        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);
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
            mockMvc.perform(
                            get(
                                            "/list-documents/{clientId}/{folderId}",
                                            client.getId(),
                                            folder.getId())
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id").value(clientDocument.getId()))
                    .andExpect(
                            jsonPath("$.[0].clientName")
                                    .value(clientDocument.getClient().getName()))
                    .andExpect(
                            jsonPath("$.[0].folderName")
                                    .value(clientDocument.getFolder().getFolderName()))
                    .andExpect(jsonPath("$.[0].blobName").value(clientDocument.getBlobName()))
                    .andExpect(jsonPath("$.[0].status").value(clientDocument.getStatus().name()))
                    .andExpect(jsonPath("$.[0].docName").value(clientDocument.getDocName()))
                    .andExpect(jsonPath("$.[0].blobUrl").value(url))
                    .andExpect(
                            jsonPath("$.[0].lastUploadedBy")
                                    .value(clientDocument.getLastUploadedBy()))
                    .andExpect(
                            jsonPath("$.[0].lastUpdatedAt")
                                    .value(
                                            clientDocument
                                                    .getLastUpdatedAt()
                                                    .atOffset(ZoneOffset.UTC)
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ list documents failure")
    void testListDocuments_Failure() {
        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);

            // When
            mockMvc.perform(
                            get("/list-documents/{clientId}/{folderId}", 500, 500)
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success", is(false)));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT / delete document")
    void testDeleteDocumentById_Success() {
        // Given
        long clientId = 1L;
        long folderId = 2L;
        Client client = Client.builder().id(clientId).name("test").build();
        client = clientRepository.save(client);
        Folder folder = Folder.builder().id(folderId).client(client).folderName("Check").build();
        folder = folderRepository.save(folder);
        String name = "photo2";
        String docName = "phto";
        ClientDocument clientDocument =
                ClientDocument.builder()
                        .client(client)
                        .folder(folder)
                        .docName(docName)
                        .status(NoticeStatus.ACTIVE)
                        .blobName(name)
                        .lastUploadedBy("Sam")
                        .lastUpdatedAt(Instant.now())
                        .build();
        clientDocumentRepository.save(clientDocument);
        // When
        mockMvc.perform(
                        put("/delete-document/{id}", clientDocument.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT / delete document failure")
    void testDeleteDocument_Failure() {
        // When
        mockMvc.perform(
                        put("/delete-document/{id}", 600)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }
}
