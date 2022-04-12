package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.dto.ClientNDISPlanRequest;
import com.onedoorway.project.dto.UpdateClientNDISPlanRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientNDISPlanRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.LocalDate;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ClientNDISPlanControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;
    @Autowired private ClientNDISPlanRepository clientNDISPlanRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClientRepository clientRepository;

    @Mock private JwtUtil mockJwtUtil;
    @Mock private ODWUserDetailsService mockUserDetailsService;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(
                                new HashSet<>(
                                        Collections.singletonList(
                                                Role.builder().id(1).name("USER").build())))
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
        clientNDISPlanRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST / create client ndis plan")
    void testCreateClientNDISPlan_Success() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        ClientNDISPlanRequest request =
                ClientNDISPlanRequest.builder()
                        .clientId(client.getId())
                        .startDate(LocalDate.of(2021, 10, 11))
                        .endDate(LocalDate.of(2022, 10, 11))
                        .fundingType("fundingType1")
                        .level("level")
                        .supportDocument("supportDocs")
                        .otherDocument("otherDocs")
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        // When
        mockMvc.perform(
                        post("/client-ndis-plan/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST / create client ndis plan failure")
    void testCreateClientNDISPlanFailure() {
        // Given
        ClientNDISPlanRequest request =
                ClientNDISPlanRequest.builder()
                        .clientId(10)
                        .startDate(LocalDate.of(2021, 8, 20))
                        .endDate(LocalDate.of(2022, 10, 15))
                        .fundingType("funding")
                        .level("level")
                        .supportDocument("supportDocs")
                        .otherDocument("otherDocs")
                        .lastUploadedBy("lastUploadedBy")
                        .build();

        // When
        mockMvc.perform(
                        post("/client-ndis-plan/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST / create client ndis plan failure without fields")
    void createClientNDISPlan_Failure_WithoutFields() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        ClientNDISPlanRequest request =
                ClientNDISPlanRequest.builder()
                        .clientId(client.getId())
                        .startDate(LocalDate.of(2021, 12, 5))
                        .endDate(LocalDate.of(2022, 12, 11))
                        .fundingType(" ")
                        .level(" ")
                        .supportDocument("supportDocs")
                        .otherDocument("otherDocs")
                        .lastUploadedBy(" ")
                        .build();
        // When
        mockMvc.perform(
                        post("/client-ndis-plan/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET /Client ndis plan ById")
    void testGetClientBNDISPlanByIdSuccess() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
        ClientNDISPlan clientNDISPlan =
                ClientNDISPlan.builder()
                        .client(client)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.parse("2023-10-10"))
                        .fundingType("fundingType")
                        .level("level")
                        .supportDocument("supportDocument")
                        .otherDocument("otherDocument")
                        .lastUploadedBy("lastUploadedBy")
                        .deleted(false)
                        .build();
        clientNDISPlanRepository.save(clientNDISPlan);

        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);
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
            // When
            mockMvc.perform(
                            get("/client-ndis-plan/list/{clientId}", client.getId())
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].id").value(clientNDISPlan.getId()))
                    .andExpect(jsonPath("$.[0].clientId").value(client.getId()))
                    .andExpect(
                            jsonPath("$.[0].startDate")
                                    .value(
                                            clientNDISPlan
                                                    .getStartDate()
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "yyyy-MM-dd"))))
                    .andExpect(
                            jsonPath("$.[0].endDate")
                                    .value(
                                            clientNDISPlan
                                                    .getEndDate()
                                                    .format(
                                                            DateTimeFormatter.ofPattern(
                                                                    "yyyy-MM-dd"))))
                    .andExpect(jsonPath("$.[0].fundingType").value(clientNDISPlan.getFundingType()))
                    .andExpect(jsonPath("$.[0].level").value(clientNDISPlan.getLevel()))
                    .andExpect(jsonPath("$.[0].deleted").value(clientNDISPlan.getDeleted()))
                    .andExpect(jsonPath("$.[0].supportDocument").value(sasSupportDocumentUrl))
                    .andExpect(jsonPath("$.[0].otherDocument").value(sasOtherDocumentUrl))
                    .andExpect(
                            jsonPath("$.[0].lastUploadedBy")
                                    .value(clientNDISPlan.getLastUploadedBy()));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get Client ndis plan failure")
    void testGetClientNDISPlan_Failure() {
        // When
        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);
            mockMvc.perform(
                            get("/client-ndis-plan/list/{clientId}", 10)
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print())

                    // Then
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success", is(false)));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client ndis plan")
    void testUpdateClientNDISPlanById_Success() {
        long id = 1;
        String startDate = "2021-04-06";
        String endDate = "2022-12-03";
        String fundType = "fundType";
        String level = "level";
        Boolean deleted = false;
        String lastUploadedBy = "Sam";
        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);
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

        clientNDISPlanRepository.save(clientNDISPlan);

        UpdateClientNDISPlanRequest request =
                UpdateClientNDISPlanRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .deleted(deleted)
                        .fundingType(fundType)
                        .level(level)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/client-ndis-plan/update/{id}", clientNDISPlan.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                //                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client ndis plan failure")
    void testUpdateClientNDISPlanById_Failure() {

        String startDate = "2021-04-06";
        String endDate = "2022-12-03";

        UpdateClientNDISPlanRequest request =
                UpdateClientNDISPlanRequest.builder().startDate(startDate).endDate(endDate).build();
        // When
        mockMvc.perform(
                        put("/client-ndis-plan/update/{id}", 100)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
